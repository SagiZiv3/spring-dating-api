package iob.logic.activities;


import iob.boundaries.ActivityBoundary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("testInvokeActivity")
@Profile("testing")
public class TestActivity implements InvokableActivity {
//    private final ObjectMapper objectMapper;
//    private final CustomizedInstancesService instancesService;
//
//    public TestActivity(ObjectMapper objectMapper, CustomizedInstancesService instancesService) {
//        this.objectMapper = objectMapper;
//        this.instancesService = instancesService;
//    }

    @Override
    public Object invoke(ActivityBoundary activityBoundary) {
        return Collections.singletonMap("status", "success");
    }
}