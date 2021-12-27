package iob.logic.activities;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userLogout")
public class UserLogoutActivity implements InvokableActivity {
    private final CustomizedInstancesService instancesService;

    @Autowired
    public UserLogoutActivity(CustomizedInstancesService instancesService) {
        this.instancesService = instancesService;
    }

    @Override
    public Object invoke(ActivityBoundary activityBoundary) {
        // This activity would simply mark the login instance as inactive
        // Step 1: Find the instance in the system
        // For this step we need to find child instances by type, and take the most recent one
        List<InstanceBoundary> loginInstances = instancesService.getChildInstancesOfType(activityBoundary.getInstance().getInstanceId(),
                "LOGIN");
        InstanceBoundary recentLogin = loginInstances.get(0);
        recentLogin.setActive(false);
        return instancesService.storeInstance(recentLogin);
    }
}