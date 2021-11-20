package com.example.demo.boundaries.converters;

import com.example.demo.boundaries.InstanceBoundary;
import com.example.demo.boundaries.Location;
import com.example.demo.data.InstanceEntity;
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
        boundary.setActive(instanceEntity.isActive());
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

    private Location toLocationBoundary(String location) {
        String[] values = location.split(";");
        double lat = Double.parseDouble(values[0]);
        double lng = Double.parseDouble(values[1]);
        return new Location(lat, lng);
    }

    private String toLocationEntity(Location location) {
        return String.format("%f;%f", location.getLat(), location.getLng());
    }
}