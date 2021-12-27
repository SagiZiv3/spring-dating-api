package iob.logic.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.CreatedByBoundary;
import iob.boundaries.helpers.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Location location = objectMapper.convertValue(
                activityBoundary.getActivityAttributes().get("location"),
                Location.class
        );
        // Create an InstanceBoundary of type "user"
        InstanceBoundary instanceBoundary = new InstanceBoundary();
        instanceBoundary.setCreatedBy(new CreatedByBoundary(activityBoundary.getInvokedBy().getUserId()));
        instanceBoundary.setActive(true);
        instanceBoundary.setType("USER");
        instanceBoundary.setName("USER_INSTANCE");
        instanceBoundary.setLocation(location);
        // Save it in the database
        InstanceBoundary storedInstance = instancesService.storeInstance(instanceBoundary);
        // Return the instance
        return storedInstance;
    }
}