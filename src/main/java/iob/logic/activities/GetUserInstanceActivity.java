package iob.logic.activities;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.helpers.UserIdBoundary;
import iob.logic.exceptions.activity.MissingUserInstanceException;
import iob.logic.instancesearching.By;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("getUserInstance")
@Slf4j
public class GetUserInstanceActivity implements InvokableActivity {
    private final CustomizedInstancesService instancesService;

    @Autowired
    public GetUserInstanceActivity(CustomizedInstancesService instancesService) {
        this.instancesService = instancesService;
    }

    @Override
    public Object invoke(ActivityBoundary activityBoundary) {
        log.info("Invoking GetUserActivity");
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
         *           "domain": "2022a.Tomer.Dwek"
         *       }
         *   }
         * }
         * */
        UserIdBoundary userId = activityBoundary.getInvokedBy().getUserId();
        By by = By.userId(userId).and(By.type(InstanceOptions.Types.USER));
        log.trace("Searching for user with id {}", userId);
        return instancesService.findEntity(by)
                .orElseThrow(() -> new MissingUserInstanceException(userId.getEmail(), userId.getDomain()));
    }
}