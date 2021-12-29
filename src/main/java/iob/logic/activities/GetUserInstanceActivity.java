package iob.logic.activities;

import iob.boundaries.ActivityBoundary;
import iob.logic.instancesearching.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("getUserInstance")
public class GetUserInstanceActivity implements InvokableActivity {
    private final CustomizedInstancesService instancesService;

    @Autowired
    public GetUserInstanceActivity(CustomizedInstancesService instancesService) {
        this.instancesService = instancesService;
    }

    @Override
    public Object invoke(ActivityBoundary activityBoundary) {
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
         *       "instanceId": { // This would always be the dummy instance
         *           "id": "1"
         *           "domain": "~APP'S DOMAIN~"
         *       }
         *   }
         * }
         * */
        By by = By.userId(activityBoundary.getInvokedBy().getUserId()).and(By.type(InstanceOptions.Types.USER));
        return instancesService.findEntity(by);
    }
}