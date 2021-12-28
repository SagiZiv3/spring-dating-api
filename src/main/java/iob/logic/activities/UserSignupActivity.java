package iob.logic.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.CreatedByBoundary;
import iob.boundaries.helpers.Location;
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
        /*
         * Activity expected structure (ignoring unnecessary attributes):
         * {
         *   "invokedBy": {
         *       "userId": {
         *           "email": "some@email.com"
         *           "domain": "some_domain"
         *       }
         *   },
         *   "activityAttributes": {
         *       "location": { // User's home address
         *           "lat": ~SOME DOUBLE~,
         *           "lng": ~SOME DOUBLE~
         *       }
         *   }
         * }
         * */
        // Extract location from activity
        log.info("Invoking user sign up activity");
        Location location;
        if (activityBoundary.getActivityAttributes().containsKey(InstanceOptions.Attributes.LOCATION)) {
            location = objectMapper.convertValue(
                    activityBoundary.getActivityAttributes().get(InstanceOptions.Attributes.LOCATION),
                    Location.class
            );
            log.debug("Specified location: {}", location);
        } else {
            location = null;
        }
        // Create an InstanceBoundary of type "user"
        InstanceBoundary instanceBoundary = new InstanceBoundary();
        instanceBoundary.setCreatedBy(new CreatedByBoundary(activityBoundary.getInvokedBy().getUserId()));
        instanceBoundary.setActive(true);
        instanceBoundary.setType(InstanceOptions.Types.USER);
        instanceBoundary.setName("USER_INSTANCE");
        instanceBoundary.setLocation(location);
        addInstanceAttributes(instanceBoundary);
        log.info("Created a user instance");
        // Save it in the database
        InstanceBoundary storedInstance = instancesService.store(instanceBoundary);
        log.debug(storedInstance.toString());
        return storedInstance;
    }

    private void addInstanceAttributes(InstanceBoundary instanceBoundary) {
        Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put(InstanceOptions.Attributes.INCOMING_LIKES, Collections.emptyList());
        instanceAttributes.put(InstanceOptions.Attributes.OUTGOING_LIKES, Collections.emptyList());
        instanceBoundary.setInstanceAttributes(instanceAttributes);
    }
}