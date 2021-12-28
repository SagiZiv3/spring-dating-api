package iob.logic.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.UserIdBoundary;
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
        /*
         * Activity expected structure (ignoring unnecessary attributes):
         * {
         *   "invokedBy": {
         *       "userId": {
         *           "email": "some@email.com"
         *           "domain": "some_domain"
         *       }
         *   },
         *  "instance": {
         *       "instanceId": {
         *           "id": "~SOME_ID~"
         *           "domain": "some_domain"
         *       }
         *   },
         *   "activityAttributes": {
         *       "otherUser": {
         *           "email": ~LIKED USER EMAIL~,
         *           "domain": ~LIKED USER DOMAIN~
         *       }
         *   }
         * }
         * */
        // We have the id of the user that created the activity, its instance id
        // and the id of the liked user.
        // First, we need to find if the invoking user and check if he already liked the other user.
        log.info("Invoking like activity");
        UserIdBoundary invokingUserId = activityBoundary.getInvokedBy().getUserId();
        UserIdBoundary likedUserId = objectMapper.convertValue(
                activityBoundary.getActivityAttributes().get("otherUser"),
                UserIdBoundary.class
        );
        if (invokingUserId.equals(likedUserId)) {
            throw new RuntimeException("User can't like himself!");
        }

        log.info("Searching for users' instances");
        By byInvokingUser = By.type("USER").and(By.id(activityBoundary.getInstance().getInstanceId()));
        By byLikedUser = By.type("USER").and(By.userId(likedUserId));
        InstanceBoundary invokingUserInstance = instancesService.findEntity(byInvokingUser);
        InstanceBoundary likedUserInstance = instancesService.findEntity(byLikedUser);
        log.debug("Invoking user: {}", invokingUserInstance);
        log.debug("Liked user: {}", likedUserInstance);

        List<UserIdBoundary> usersILiked = getLikesForUser(KEYS.OUTGOING_LIKES, invokingUserInstance);
        List<UserIdBoundary> usersThatLikedTheOtherUser = getLikesForUser(KEYS.INCOMING_LIKES, likedUserInstance);

        // If liked for second time, assume unlike and remove the like from both users and return no match
        if (usersILiked.contains(likedUserId)) {
            usersILiked.remove(likedUserId);
            usersThatLikedTheOtherUser.remove(invokingUserId);
            saveAttributesChanges(invokingUserInstance, usersILiked, KEYS.OUTGOING_LIKES);
            saveAttributesChanges(likedUserInstance, usersThatLikedTheOtherUser, KEYS.INCOMING_LIKES);
            return Collections.singletonMap("isMatch", false);
        }

        // If not already liked, add the like to the users and check if the other user liked the invoking user.
        usersILiked.add(likedUserId);
        usersThatLikedTheOtherUser.add(invokingUserId);
        saveAttributesChanges(invokingUserInstance, usersILiked, KEYS.OUTGOING_LIKES);
        saveAttributesChanges(likedUserInstance, usersThatLikedTheOtherUser, KEYS.INCOMING_LIKES);
        boolean isMatch = getLikesForUser(KEYS.OUTGOING_LIKES, likedUserInstance).contains(invokingUserId);
        return Collections.singletonMap("isMatch", isMatch);
    }

    private List<UserIdBoundary> getLikesForUser(String likesType, InstanceBoundary userInstance) {
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
        String INCOMING_LIKES = "incoming_likes";
        String OUTGOING_LIKES = "outgoing_likes";
    }
}