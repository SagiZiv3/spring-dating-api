package com.example.demo.boundaries.converters;

import com.example.demo.boundaries.InstanceBoundary;
import com.example.demo.boundaries.Location;
import com.example.demo.boundaries.ObjectId;
import com.example.demo.data.InstanceEntity;
import org.springframework.stereotype.Component;

@Component
public class InstanceConverter {
    public InstanceBoundary toUserBoundary(InstanceEntity instanceEntity) {
        InstanceBoundary boundary = new InstanceBoundary();
        boundary.setName(instanceEntity.getName());
        boundary.setType(instanceEntity.getType());
        boundary.setInstanceAttributes(instanceEntity.getInstanceAttributes());
        boundary.setCreatedTimestamp(instanceEntity.getCreatedTimestamp());
        boundary.setActive(instanceEntity.isActive());
        boundary.setCreatedBy(instanceEntity.getCreatedBy());
        boundary.setLocation(toLocationBoundary(instanceEntity.getLocation()));
        boundary.setInstanceId(toInstanceIdBoundary(instanceEntity.getInstanceId()));
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
        entity.setCreatedBy(instanceBoundary.getCreatedBy());
        entity.setLocation(toLocationEntity(instanceBoundary.getLocation()));
        entity.setInstanceId(toInstanceIdEntity(instanceBoundary.getInstanceId()));
        return entity;
    }

    private String toInstanceIdEntity(ObjectId instanceId) {
        return String.format("%s;%s", instanceId.getDomain(), instanceId.getId());
    }

    private String toLocationEntity(Location location) {
        return String.format("%f;%f", location.getLat(), location.getLng());
    }

    private ObjectId toInstanceIdBoundary(String instanceId) {
        String[] values = instanceId.split(";");
        String domain = values[0];
        String id = values[1];
        return new ObjectId(domain, id);
    }

    private Location toLocationBoundary(String location) {
        String[] values = location.split(";");
        double lat = Double.parseDouble(values[0]);
        double lng = Double.parseDouble(values[1]);
        return new Location(lat, lng);
    }
}