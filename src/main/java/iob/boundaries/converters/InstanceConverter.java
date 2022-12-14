package iob.boundaries.converters;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.CreatedByBoundary;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.boundaries.helpers.Location;
import iob.data.InstanceEntity;
import iob.data.embeddedentities.LocationEntity;
import iob.data.primarykeys.InstancePrimaryKey;
import iob.data.primarykeys.UserPrimaryKey;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstanceConverter {
    private final UserConverter userConverter;

    @Autowired
    public InstanceConverter(UserConverter userConverter) {
        this.userConverter = userConverter;
    }

    public InstanceBoundary toBoundary(InstanceEntity entity) {
        InstanceBoundary boundary = new InstanceBoundary();
        boundary.setName(entity.getName());
        boundary.setType(entity.getType());
        boundary.setInstanceAttributes(entity.getInstanceAttributes());
        boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
        boundary.setActive(entity.getActive());
        boundary.setLocation(toLocationBoundary(entity.getLocation()));
        boundary.setCreatedBy(toCreatedByBoundary(entity.getCreatedBy()));
        boundary.setInstanceId(toInstanceIdBoundary(entity.getDomain(), entity.getId()));
        return boundary;
    }

    public List<InstanceBoundary> toBoundaries(Collection<InstanceEntity> instanceEntities) {
        return instanceEntities.stream()
                .map(this::toBoundary)
                .collect(Collectors.toList());
    }

    public InstanceEntity toEntity(InstanceBoundary boundary) {
        InstanceEntity entity = new InstanceEntity();
        entity.setName(boundary.getName());
        entity.setType(boundary.getType());
        entity.setInstanceAttributes(boundary.getInstanceAttributes());
        entity.setCreatedTimestamp(boundary.getCreatedTimestamp());
        entity.setActive(boundary.getActive() != null && boundary.getActive());
        entity.setLocation(toLocationEntity(boundary.getLocation()));
        entity.setCreatedBy(toCreatedByEntity(boundary.getCreatedBy()));

        if (boundary.getInstanceId() != null) {
            InstancePrimaryKey instanceKey = toInstancePrimaryKey(boundary.getInstanceId());
            entity.setDomain(instanceKey.getDomain());
            entity.setId(instanceKey.getId());
        }
        return entity;
    }

    private CreatedByBoundary toCreatedByBoundary(UserPrimaryKey createdBy) {
        return new CreatedByBoundary(userConverter.toUserIdBoundary(createdBy.getDomain(), createdBy.getEmail()));
    }

    public UserPrimaryKey toCreatedByEntity(CreatedByBoundary createdBy) {
        if (createdBy == null) {
            return null;
        }
        UserPrimaryKey entity = new UserPrimaryKey();
        entity.setEmail(createdBy.getUserId().getEmail());
        entity.setDomain(createdBy.getUserId().getDomain());
        return entity;
    }

    public InstanceIdBoundary toInstanceIdBoundary(String domain, long id) {
        return new InstanceIdBoundary(domain, Long.toString(id));
    }

    public InstancePrimaryKey toInstancePrimaryKey(@NonNull InstanceIdBoundary instanceIdBoundary) {
        return toInstancePrimaryKey(instanceIdBoundary.getId(), instanceIdBoundary.getDomain());
    }

    public InstancePrimaryKey toInstancePrimaryKey(String id, String domain) {
        return new InstancePrimaryKey(Long.parseLong(id), domain);
    }

    public LocationEntity toLocationEntity(Location location) {
        if (location == null) return null;
        return new LocationEntity(location.getLat(), location.getLng());
    }

    private Location toLocationBoundary(LocationEntity location) {
        if (location == null) return null;
        return new Location(location.getLocationLat(), location.getLocationLng());
    }
}