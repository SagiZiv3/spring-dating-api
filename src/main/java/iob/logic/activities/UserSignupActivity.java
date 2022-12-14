package iob.logic.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.CreatedByBoundary;
import iob.boundaries.helpers.Location;
import iob.logic.exceptions.activity.MultipleLoginsException;
import iob.logic.instancesearching.By;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service("userSignup")
@Slf4j
public class UserSignupActivity implements InvokableActivity {
    private final CustomizedInstancesService instancesService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserSignupActivity(CustomizedInstancesService instancesService, ObjectMapper objectMapper) {
        this.instancesService = instancesService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Object invoke(ActivityBoundary activityBoundary) {
        log.info("Invoking user signup activity");
        /*
         * Activity expected structure (ignoring unnecessary attributes):
         * {
         *   "invokedBy": {
         *       "userId": { // The user you want to sign in to the system
         *           "email": "some@email.com"
         *           "domain": "some_domain"
         *       }
         *   },
         *  "instance": {
         *       "instanceId": { // This would always be the dummy instance
         *           "id": "1"
         *           "domain": "2022a.Tomer.Dwek"
         *       }
         *   },
         *   "activityAttributes": {
         *       "location": { // User's home address (optional input)
         *           "lat": ~SOME DOUBLE~,
         *           "lng": ~SOME DOUBLE~
         *       }
         *   }
         * }
         * */
        By by = By.userId(activityBoundary.getInvokedBy().getUserId())
                .and(By.type(InstanceOptions.Types.USER))
                .and(By.activeIn(Collections.singleton(true)));

        // If there is already an active user instance, throw an exception.
        if (instancesService.findEntity(by).isPresent()) {
            log.error("Multiple signups detected");
            throw new MultipleLoginsException();
        }
        InstanceBoundary instanceBoundary = createUserInstance(activityBoundary);
        log.info("Created a user instance");
        // Save it in the database
        InstanceBoundary storedInstance = instancesService.store(instanceBoundary);
        log.debug(storedInstance.toString());
        return storedInstance;
    }

    private InstanceBoundary createUserInstance(ActivityBoundary activityBoundary) {
        log.trace("Creating user instance for user {}", activityBoundary.getInvokedBy().getUserId());
        Location location = extractLocation(activityBoundary);
        // Create an InstanceBoundary of type "user"
        InstanceBoundary instanceBoundary = new InstanceBoundary();
        instanceBoundary.setCreatedBy(new CreatedByBoundary(activityBoundary.getInvokedBy().getUserId()));
        instanceBoundary.setActive(true);
        instanceBoundary.setType(InstanceOptions.Types.USER);
        instanceBoundary.setName("USER_INSTANCE");
        instanceBoundary.setLocation(location);
        addInstanceAttributes(instanceBoundary);
        return instanceBoundary;
    }

    private Location extractLocation(ActivityBoundary activityBoundary) {
        Location location;
        if (activityBoundary.getActivityAttributes().containsKey(InstanceOptions.Attributes.LOCATION)) {
            location = objectMapper.convertValue(
                    activityBoundary.getActivityAttributes().get(InstanceOptions.Attributes.LOCATION),
                    Location.class
            );
            log.debug("Specified location: {}", location);
        } else {
            log.warn("Location wasn't specified in activity");
            location = new Location();
        }
        return location;
    }

    private void addInstanceAttributes(InstanceBoundary instanceBoundary) {
        Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put(InstanceOptions.Attributes.INCOMING_LIKES, Collections.emptyList());
        instanceAttributes.put(InstanceOptions.Attributes.OUTGOING_LIKES, Collections.emptyList());
        instanceBoundary.setInstanceAttributes(instanceAttributes);
    }
}