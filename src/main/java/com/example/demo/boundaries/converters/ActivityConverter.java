package com.example.demo.boundaries.converters;

import com.example.demo.boundaries.ActivityBoundary;
import com.example.demo.boundaries.ObjectId;
import com.example.demo.data.ActivityEntity;
import org.springframework.stereotype.Component;

@Component
public class ActivityConverter {
    public ActivityBoundary toActivityBoundary(ActivityEntity entity) {
        ActivityBoundary boundary = new ActivityBoundary();
        boundary.setType(entity.getType());
        boundary.setActivityAttributes(entity.getActivityAttributes());
        boundary.setInstance(entity.getInstance());
        boundary.setInvokedBy(entity.getInvokedBy());
        boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
        boundary.setActivityId(toInstanceIdBoundary(entity.getActivityId()));
        return boundary;
    }

    public ActivityEntity toActivityEntity(ActivityBoundary boundary) {
        ActivityEntity entity = new ActivityEntity();
        entity.setType(boundary.getType());
        entity.setActivityAttributes(boundary.getActivityAttributes());
        entity.setInstance(boundary.getInstance());
        entity.setInvokedBy(boundary.getInvokedBy());
        entity.setCreatedTimestamp(boundary.getCreatedTimestamp());
        entity.setActivityId(toInstanceIdEntity(boundary.getActivityId()));
        return entity;
    }

    private String toInstanceIdEntity(ObjectId instanceId) {
        return String.format("%s;%s", instanceId.getDomain(), instanceId.getId());
    }

    private ObjectId toInstanceIdBoundary(String instanceId) {
        String[] values = instanceId.split(";");
        String domain = values[0];
        String id = values[1];
        return new ObjectId(domain, id);
    }
}