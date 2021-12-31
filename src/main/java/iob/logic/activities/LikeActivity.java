package iob.logic.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.UserIdBoundary;
import iob.logic.exceptions.activity.BrokenUserInstanceException;
import iob.logic.exceptions.activity.InvalidLikeActivityException;
import iob.logic.exceptions.activity.MissingUserInstanceException;
import iob.logic.instancesearching.By;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("like")
@Slf4j
public class LikeActivity implements InvokableActivity {
    private final CustomizedInstancesService instancesService;
    private final ObjectMapper objectMapper;

    public LikeActivity(CustomizedInstancesService instancesService, ObjectMapper objectMapper) {
        this.instancesService = instancesService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Object invoke(ActivityBoundary activityBoundary) {
        log.info("Invoking like activity");
        /*
         * Activity expected structure (ignoring unnecessary attributes):
         * {
         *   "invokedBy": {
         *       "userId": {
         *           "email": "~USER'S EMAIL~"
         *           "domain": "~USER'S DOMAIN~"
         *       }
         *   },
         *  "instance": {
         *       "instanceId": { // This would be the instance representing the invoking user
         *           "id": "~SOME_ID~"
         *           "domain": "some_domain"
         *       }
         *   },
         *   "activityAttributes": {
         *       "otherUser": { // The information about the liked user
         *           "email": ~LIKED USER EMAIL~,
         *           "domain": ~LIKED USER DOMAIN~
         *       }
         *   }
         * }
         * */
        // We have the id of the user that created the activity, its instance id
        // and the id of the liked user.
        // First, we need to find the users in the database.
        UserIdBoundary invokingUserId = activityBoundary.getInvokedBy().getUserId();
        UserIdBoundary likedUserId = objectMapper.convertValue(
                activityBoundary.getActivityAttributes().get(InstanceOptions.Attributes.OTHER_USER),
                UserIdBoundary.class
        );
        if (invokingUserId.equals(likedUserId)) {
            throw new InvalidLikeActivityException(invokingUserId.getEmail(), invokingUserId.getDomain());
        }

        log.info("Searching for users' instances");
        By byInvokingUser = By.type(InstanceOptions.Types.USER).and(By.id(activityBoundary.getInstance().getInstanceId()));
        By byLikedUser = By.type(InstanceOptions.Types.USER).and(By.userId(likedUserId));
        InstanceBoundary invokingUserInstance = instancesService.findEntity(byInvokingUser)
                .orElseThrow(() -> new MissingUserInstanceException(invokingUserId.getEmail(), invokingUserId.getDomain()));

        InstanceBoundary likedUserInstance = instancesService.findEntity(byLikedUser)
                .orElseThrow(() -> new MissingUserInstanceException(likedUserId.getEmail(), likedUserId.getDomain()));

        log.debug("Invoking user: {}", invokingUserInstance);
        log.debug("Liked user: {}", likedUserInstance);

        List<UserIdBoundary> usersILiked = getLikesForUser(InstanceOptions.Attributes.OUTGOING_LIKES, invokingUserInstance);
        List<UserIdBoundary> usersThatLikedTheOtherUser = getLikesForUser(InstanceOptions.Attributes.INCOMING_LIKES, likedUserInstance);

        // If liked for second time, assume unlike and remove the like from both users and return no match
        if (usersILiked.contains(likedUserId)) {
            return removeLike(invokingUserId, likedUserId, invokingUserInstance,
                    likedUserInstance, usersILiked, usersThatLikedTheOtherUser);
        }

        // If not already liked, add the like to the users and check if the other user liked the invoking user.
        return addLike(invokingUserId, likedUserId, invokingUserInstance,
                likedUserInstance, usersILiked, usersThatLikedTheOtherUser);
    }

    private Map<String, Boolean> removeLike(UserIdBoundary invokingUserId, UserIdBoundary likedUserId, InstanceBoundary invokingUserInstance, InstanceBoundary likedUserInstance, List<UserIdBoundary> usersILiked, List<UserIdBoundary> usersThatLikedTheOtherUser) {
        usersILiked.remove(likedUserId);
        usersThatLikedTheOtherUser.remove(invokingUserId);
        saveAttributesChanges(invokingUserInstance, usersILiked, InstanceOptions.Attributes.OUTGOING_LIKES);
        saveAttributesChanges(likedUserInstance, usersThatLikedTheOtherUser, InstanceOptions.Attributes.INCOMING_LIKES);
        return Collections.singletonMap(KEYS.IS_MATCH, false);
    }

    private Map<String, Boolean> addLike(UserIdBoundary invokingUserId, UserIdBoundary likedUserId, InstanceBoundary invokingUserInstance, InstanceBoundary likedUserInstance, List<UserIdBoundary> usersILiked, List<UserIdBoundary> usersThatLikedTheOtherUser) {
        usersILiked.add(likedUserId);
        usersThatLikedTheOtherUser.add(invokingUserId);
        saveAttributesChanges(invokingUserInstance, usersILiked, InstanceOptions.Attributes.OUTGOING_LIKES);
        saveAttributesChanges(likedUserInstance, usersThatLikedTheOtherUser, InstanceOptions.Attributes.INCOMING_LIKES);
        // Check if the liked user also liked the invoking user
        boolean isMatch = getLikesForUser(InstanceOptions.Attributes.OUTGOING_LIKES, likedUserInstance).contains(invokingUserId);
        return Collections.singletonMap(KEYS.IS_MATCH, isMatch);
    }

    private List<UserIdBoundary> getLikesForUser(String likesType, InstanceBoundary userInstance) {
        if (!userInstance.getInstanceAttributes().containsKey(likesType)) {
            UserIdBoundary userId = userInstance.getCreatedBy().getUserId();
            throw new BrokenUserInstanceException(userId.getEmail(), userId.getDomain(), likesType);
        }
        UserIdBoundary[] asArray = objectMapper.convertValue(
                userInstance.getInstanceAttributes().get(likesType),
                UserIdBoundary[].class
        );
        return new ArrayList<>(Arrays.asList(asArray));
    }

    private void saveAttributesChanges(InstanceBoundary userInstance, List<UserIdBoundary> userIds, String likeType) {
        Map<String, Object> instanceAttributes = new HashMap<>(userInstance.getInstanceAttributes());
        instanceAttributes.put(likeType, userIds);
        userInstance.setInstanceAttributes(instanceAttributes);
        instancesService.update(userInstance);
    }

    private interface KEYS {
        String IS_MATCH = "is_match";
    }
}