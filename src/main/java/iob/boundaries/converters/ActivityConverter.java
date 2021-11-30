package iob.boundaries.converters;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.helpers.Instance;
import iob.boundaries.helpers.ObjectId;
import iob.data.ActivityEntity;
import iob.data.InstanceEntity;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
        boundary.setInvokedBy(idsConverter.toUserIdMapBoundary(entity.getInvokedBy()));
        boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
        boundary.setActivityId(new ObjectId(entity.getDomain(), Long.toString(entity.getId())));
        return boundary;
    }

    private Instance toInstanceBoundary(InstanceEntity instance) {
//        String[] parameters = createdBy.get("userId").split(delimiter);
//        return new Instance(new ObjectId(parameters[0], parameters[1]));
        return new Instance(new ObjectId(
                instance.getDomain(),
                Long.toString(instance.getId())
        ));
    }

    public ActivityEntity toActivityEntity(ActivityBoundary boundary) {
        ActivityEntity entity = new ActivityEntity();
        entity.setType(boundary.getType());
        entity.setActivityAttributes(boundary.getActivityAttributes());
        entity.setInstance(toInstanceEntity(boundary.getInstance()));
        entity.setInvokedBy(idsConverter.toUserIdMapEntity(boundary.getInvokedBy()));
        entity.setCreatedTimestamp(boundary.getCreatedTimestamp());
        if (boundary.getActivityId() != null) {
            entity.setDomain(boundary.getActivityId().getDomain());
            entity.setId(Long.parseLong(boundary.getActivityId().getId()));
        }
        return entity;
    }

    private InstanceEntity toInstanceEntity(Instance instance) {
        InstanceEntity entity = new InstanceEntity();
        entity.setId(Long.parseLong(instance.getInstanceId().getId()));
        entity.setDomain(instance.getInstanceId().getDomain());
//        Map<String, String> entity = new HashMap<>();
//        entity.put("instanceId", createdBy.getInstanceId().getDomain() + delimiter + createdBy.getInstanceId().getId());
        return entity;
    }
}