package iob.logic.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.CreatedByBoundary;
import iob.boundaries.helpers.Location;
import iob.boundaries.helpers.UserIdBoundary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service("userSignup")
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
         *       "location": {
         *           "lat": ~SOME DOUBLE~,
         *           "lng": ~SOME DOUBLE~
         *       }
         *   }
         * }
         * */
        // Extract location from activity
        // The location is the user's home address.
        System.err.println("Invoking user sign up activity");
        Location location = objectMapper.convertValue(
                activityBoundary.getActivityAttributes().get("location"),
                Location.class
        );
        System.err.printf("Specified location: %s%n", location);
        // Create an InstanceBoundary of type "user"
        InstanceBoundary instanceBoundary = new InstanceBoundary();
        instanceBoundary.setCreatedBy(new CreatedByBoundary(activityBoundary.getInvokedBy().getUserId()));
        instanceBoundary.setActive(true);
        instanceBoundary.setType("USER");
        instanceBoundary.setName("USER_INSTANCE");
        instanceBoundary.setLocation(location);
        Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put("incoming_likes", new ArrayList<UserIdBoundary>());
        instanceAttributes.put("outgoing_likes", new ArrayList<UserIdBoundary>());
        instanceBoundary.setInstanceAttributes(instanceAttributes);
        System.err.printf("Created instance: %s%n", instanceBoundary);
        // Save it in the database
        InstanceBoundary storedInstance = instancesService.store(instanceBoundary);
        // Return the instance
        return storedInstance;
    }
}