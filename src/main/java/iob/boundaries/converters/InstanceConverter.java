package iob.boundaries.converters;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.Location;
import iob.boundaries.helpers.ObjectId;
import iob.data.InstanceEntity;
import iob.data.embeddedentities.LocationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InstanceConverter {
    private final IdsConverter idsConverter;

    @Autowired
    public InstanceConverter(IdsConverter idsConverter) {
        this.idsConverter = idsConverter;
    }

    public InstanceBoundary toInstanceBoundary(InstanceEntity instanceEntity) {
        InstanceBoundary boundary = new InstanceBoundary();
        boundary.setName(instanceEntity.getName());
        boundary.setType(instanceEntity.getType());
        boundary.setInstanceAttributes(instanceEntity.getInstanceAttributes());
        boundary.setCreatedTimestamp(instanceEntity.getCreatedTimestamp());
        boundary.setActive(instanceEntity.getActive());
        boundary.setLocation(toLocationBoundary(instanceEntity.getLocation()));
        boundary.setCreatedBy(idsConverter.toUserIdMapBoundary(instanceEntity.getCreatedBy()));
        boundary.setInstanceId(new ObjectId(instanceEntity.getDomain(), Long.toString(instanceEntity.getId())));
        return boundary;
    }

    public InstanceEntity toInstanceEntity(InstanceBoundary instanceBoundary) {
        InstanceEntity entity = new InstanceEntity();
        entity.setName(instanceBoundary.getName());
        entity.setType(instanceBoundary.getType());
        entity.setInstanceAttributes(instanceBoundary.getInstanceAttributes());
        entity.setCreatedTimestamp(instanceBoundary.getCreatedTimestamp());
        if (instanceBoundary.getActive() != null)
            entity.setActive(instanceBoundary.getActive());
        else
            entity.setActive(false);
        entity.setLocation(toLocationEntity(instanceBoundary.getLocation()));
        entity.setCreatedBy(idsConverter.toUserIdMapEntity(instanceBoundary.getCreatedBy()));

        if (instanceBoundary.getInstanceId() != null) {
            entity.setDomain(instanceBoundary.getInstanceId().getDomain());
            entity.setId(Long.parseLong(instanceBoundary.getInstanceId().getId()));
        }
        return entity;
    }

    public Location toLocationBoundary(LocationEntity location) {
        if (location == null) return null;
        return new Location(location.getLocationLat(), location.getLocationLng());
    }

    public LocationEntity toLocationEntity(Location location) {
        if (location == null) return null;
        return new LocationEntity(location.getLat(), location.getLng());
    }
}