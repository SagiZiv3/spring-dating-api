package iob.logic.activities;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import iob.logic.exceptions.activity.MissingLoginInstanceException;
import iob.logic.instancesearching.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("userLogout")
public class UserLogoutActivity implements InvokableActivity {
    private final CustomizedInstancesService instancesService;

    @Autowired
    public UserLogoutActivity(CustomizedInstancesService instancesService) {
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
         *       "instanceId": { // This would be the instance representing the invoking user
         *           "id": "~SOME_ID~"
         *           "domain": "some_domain"
         *       }
         *   }
         * }
         * */
        // This activity would simply mark the login instance as inactive
        // For simplicity, we suppose that the user logs in only from one device, therefore there is only one active instance.
        // So, we need to find the active login instance and deactivate it.
        By by = By.childOf(activityBoundary.getInstance().getInstanceId())
                .and(By.type(InstanceOptions.Types.USER_LOGIN))
                .and(By.activeIn(Collections.singleton(true)));
        InstanceBoundary recentLogin = instancesService.findEntity(by)
                .orElseThrow(MissingLoginInstanceException::new); // Should never happen
        recentLogin.setActive(false);
        return instancesService.update(recentLogin);
    }
}