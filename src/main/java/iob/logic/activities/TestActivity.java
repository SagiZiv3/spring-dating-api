package iob.logic.activities;


import iob.boundaries.ActivityBoundary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("testInvokeActivity")
@Profile("testing")
public class TestActivity implements InvokableActivity {
    @Override
    public Object invoke(ActivityBoundary activityBoundary) {
        return Collections.singletonMap("status", "success");
    }
}