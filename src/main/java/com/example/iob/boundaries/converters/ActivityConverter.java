package com.example.iob.boundaries.converters;

import com.example.iob.boundaries.ActivityBoundary;
import com.example.iob.boundaries.ObjectId;
import com.example.iob.data.ActivityEntity;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ActivityConverter {
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
        boundary.setActivityId(idsConverter.toObjectIdBoundary(entity.getActivityId()));
        return boundary;
    }

    public ActivityEntity toActivityEntity(ActivityBoundary boundary) {
        ActivityEntity entity = new ActivityEntity();
        entity.setType(boundary.getType());
        entity.setActivityAttributes(boundary.getActivityAttributes());
        entity.setInstance(toInstanceEntity(boundary.getInstance()));
        entity.setInvokedBy(idsConverter.toUserIdMapEntity(boundary.getInvokedBy()));
        entity.setCreatedTimestamp(boundary.getCreatedTimestamp());
        entity.setActivityId(idsConverter.toObjectIdEntity(boundary.getActivityId()));
        return entity;
    }

    private Map<String, String> toInstanceEntity(Map<String, ObjectId> createdBy) {
        // Source: https://stackoverflow.com/a/22744309
        return createdBy.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> idsConverter.toObjectIdEntity(e.getValue())));
    }

    private Map<String, ObjectId> toInstanceBoundary(Map<String, String> createdBy) {
        // Source: https://stackoverflow.com/a/22744309
        return createdBy.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> idsConverter.toObjectIdBoundary(e.getValue())));
    }
}