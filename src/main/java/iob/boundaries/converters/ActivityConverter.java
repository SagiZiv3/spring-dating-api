package iob.boundaries.converters;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.helpers.Instance;
import iob.boundaries.helpers.ObjectId;
import iob.data.ActivityEntity;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ActivityConverter {
    @Value("${application.entity.delimiter}")
    private String delimiter;
    private final IdsConverter idsConverter;

    @Autowired
    public ActivityConverter(IdsConverter idsConverter) {
        this.idsConverter = idsConverter;
    }

    public ActivityBoundary toActivityBoundary(@NonNull ActivityEntity entity) {
        ActivityBoundary boundary = new ActivityBoundary();
        boundary.setType(entity.getType());
        boundary.setActivityAttributes(entity.getActivityAttributes());
        boundary.setInstance(toInstanceBoundary(entity.getInstance()));
//        boundary.setInvokedBy(idsConverter.toUserIdMapBoundary(entity.getInvokedBy())); TODO
        boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
//        boundary.setActivityId(idsConverter.toObjectIdBoundary(entity.getActivityId()));
        return boundary;
    }

    public ActivityEntity toActivityEntity(ActivityBoundary boundary) {
        ActivityEntity entity = new ActivityEntity();
        entity.setType(boundary.getType());
        entity.setActivityAttributes(boundary.getActivityAttributes());
        entity.setInstance(toInstanceEntity(boundary.getInstance()));
//        entity.setInvokedBy(idsConverter.toUserIdMapEntity(boundary.getInvokedBy())); TODO
        entity.setCreatedTimestamp(boundary.getCreatedTimestamp());
        entity.setActivityId(idsConverter.toObjectIdEntity(boundary.getActivityId()));
        return entity;
    }

    private Map<String, String> toInstanceEntity(Instance createdBy) {
        Map<String, String> entity = new HashMap<>();
        entity.put("instanceId", createdBy.getInstanceId().getDomain() + delimiter + createdBy.getInstanceId().getId());
        return entity;
    }

    private Instance toInstanceBoundary(Map<String, String> createdBy) {
        String[] parameters = createdBy.get("userId").split(delimiter);
        return new Instance(new ObjectId(parameters[0], parameters[1]));
    }
}