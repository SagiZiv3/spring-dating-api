package iob.boundaries.converters;

import iob.boundaries.ActivityBoundary;
import iob.boundaries.helpers.ActivityIdBoundary;
import iob.boundaries.helpers.InstanceIdWrapper;
import iob.boundaries.helpers.InvokedByBoundary;
import iob.data.ActivityEntity;
import iob.data.primarykeys.ActivityPrimaryKey;
import iob.data.primarykeys.InstancePrimaryKey;
import iob.data.primarykeys.UserPrimaryKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivityConverter {
    private final UserConverter userConverter;
    private final InstanceConverter instanceConverter;

    @Autowired
    public ActivityConverter(UserConverter userConverter, InstanceConverter instanceConverter) {
        this.userConverter = userConverter;
        this.instanceConverter = instanceConverter;
    }

    public ActivityBoundary toBoundary(ActivityEntity entity) {
        ActivityBoundary boundary = new ActivityBoundary();

        boundary.setType(entity.getType());
        boundary.setActivityAttributes(entity.getActivityAttributes());
        boundary.setInstance(toInstanceIdBoundary(entity.getInstance()));
        boundary.setInvokedBy(toInvokedByBoundary(entity.getInvokedBy()));
        boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
        boundary.setActivityId(toActivityIdBoundary(entity.getDomain(), entity.getId()));

        return boundary;
    }

    private InstanceIdWrapper toInstanceIdBoundary(InstancePrimaryKey instanceEntity) {
        return new InstanceIdWrapper(instanceConverter.toInstanceIdBoundary(instanceEntity.getDomain(), instanceEntity.getId()));
    }

    private InvokedByBoundary toInvokedByBoundary(UserPrimaryKey userEntity) {
        return new InvokedByBoundary(userConverter.toUserIdBoundary(userEntity.getDomain(), userEntity.getEmail()));
    }

    public ActivityIdBoundary toActivityIdBoundary(String domain, long id) {
        return new ActivityIdBoundary(domain, Long.toString(id));
    }

    public ActivityEntity toEntity(ActivityBoundary boundary) {
        ActivityEntity entity = new ActivityEntity();
        entity.setType(boundary.getType());
        entity.setActivityAttributes(boundary.getActivityAttributes());
        entity.setInstance(toInstanceIdEntity(boundary.getInstance()));
        entity.setInvokedBy(toInvokedByEntity(boundary.getInvokedBy()));
        entity.setCreatedTimestamp(boundary.getCreatedTimestamp());
        if (boundary.getActivityId() != null) {
            ActivityPrimaryKey entityKey = toActivityPrimaryKey(boundary.getActivityId());
            entity.setDomain(entityKey.getDomain());
            entity.setId(entityKey.getId());
        }
        return entity;
    }

    private InstancePrimaryKey toInstanceIdEntity(InstanceIdWrapper instanceId) {
        InstancePrimaryKey entity = new InstancePrimaryKey();
        entity.setDomain(instanceId.getInstanceId().getDomain());
        entity.setId(Long.parseLong(instanceId.getInstanceId().getId()));
        return entity;
    }

    private UserPrimaryKey toInvokedByEntity(InvokedByBoundary invokedBy) {
        UserPrimaryKey entity = new UserPrimaryKey();
        entity.setDomain(invokedBy.getUserId().getDomain());
        entity.setEmail(invokedBy.getUserId().getEmail());
        return entity;
    }

    public ActivityPrimaryKey toActivityPrimaryKey(ActivityIdBoundary activityIdBoundary) {
        return new ActivityPrimaryKey(Long.parseLong(activityIdBoundary.getId()), activityIdBoundary.getDomain());
    }
}