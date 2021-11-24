package iob.boundaries.converters;

import iob.boundaries.InstanceBoundary;
import iob.boundaries.helpers.Location;
import iob.data.InstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InstanceConverter {
    @Value("${application.entity.delimiter}")
    private String delimiter;
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
        boundary.setInstanceId(idsConverter.toObjectIdBoundary(instanceEntity.getInstanceId()));
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
        entity.setInstanceId(idsConverter.toObjectIdEntity(instanceBoundary.getInstanceId()));
        return entity;
    }

    public Location toLocationBoundary(String location) {
        if (location == null) return null;
        String[] values = location.split(delimiter);
        double lat = Double.parseDouble(values[0]);
        double lng = Double.parseDouble(values[1]);
        return new Location(lat, lng);
    }

    public String toLocationEntity(Location location) {
        if (location == null) return null;
        return String.format("%f%s%f", location.getLat(), delimiter, location.getLng());
    }
}