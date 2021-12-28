package iob.logic.activities;


import com.fasterxml.jackson.databind.ObjectMapper;
import iob.boundaries.ActivityBoundary;
import iob.boundaries.InstanceBoundary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("createInstance")
@Profile("tests")
public class CreateCustomInstanceActivity implements InvokableActivity {
    private final ObjectMapper objectMapper;
    private final CustomizedInstancesService instancesService;

    public CreateCustomInstanceActivity(ObjectMapper objectMapper, CustomizedInstancesService instancesService) {
        this.objectMapper = objectMapper;
        this.instancesService = instancesService;
    }

    @Override
    public Object invoke(ActivityBoundary activityBoundary) {
        InstanceBoundary boundary = objectMapper.convertValue(activityBoundary.getActivityAttributes().get("instance"), InstanceBoundary.class);
        return instancesService.store(boundary);
    }
}