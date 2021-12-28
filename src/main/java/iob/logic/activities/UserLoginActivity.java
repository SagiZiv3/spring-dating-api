package iob.logic.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.CreatedByBoundary;
import iob.boundaries.helpers.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userLogin")
public class UserLoginActivity implements InvokableActivity {
    private final CustomizedInstancesService instancesService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserLoginActivity(CustomizedInstancesService instancesService, ObjectMapper objectMapper) {
        this.instancesService = instancesService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Object invoke(ActivityBoundary activityBoundary) {
        // In the login stage, we have the UserBoundary and the InstanceBoundary (of type "user").
        // We want to create a new InstanceBoundary under the provided one.
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
         *       "location": {
         *           "lat": ~SOME DOUBLE~,
         *           "lng": ~SOME DOUBLE~
         *       }
         *   }
         * }
         * */
        // Extract location from activity
        // The location is the user's current location.
        Location location = objectMapper.convertValue(
                activityBoundary.getActivityAttributes().get("location"),
                Location.class
        );
        // Create an InstanceBoundary of type "user"
        InstanceBoundary instanceBoundary = new InstanceBoundary();
        instanceBoundary.setCreatedBy(new CreatedByBoundary(activityBoundary.getInvokedBy().getUserId()));
        instanceBoundary.setActive(true);
        instanceBoundary.setType("LOGIN");
        instanceBoundary.setName("USER_LOGIN");
        instanceBoundary.setLocation(location);
        // Save it in the database
        InstanceBoundary storedInstance = instancesService.store(instanceBoundary);
        instancesService.bindInstances(activityBoundary.getInstance().getInstanceId(),
                storedInstance.getInstanceId());
        // Return the instance
        return storedInstance;
    }
}