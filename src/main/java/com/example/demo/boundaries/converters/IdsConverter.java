package com.example.demo.boundaries.converters;

import com.example.demo.boundaries.ObjectId;
import com.example.demo.boundaries.UserId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IdsConverter {

    public Map<String, String> toUserIdMapEntity(Map<String, UserId> createdBy) {
        // Source: https://stackoverflow.com/a/22744309
        return createdBy.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> toUserIdEntity(e.getValue())));
    }

    public Map<String, UserId> toUserIdMapBoundary(Map<String, String> createdBy) {
        // Source: https://stackoverflow.com/a/22744309
        return createdBy.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> toUserIdBoundary(e.getValue())));
    }

    public ObjectId toObjectIdBoundary(String instanceId) {
        String[] values = instanceId.split(";");
        String domain = values[0];
        String id = values[1];
        return new ObjectId(domain, id);
    }

    public String toObjectIdEntity(ObjectId instanceId) {
        return String.format("%s;%s", instanceId.getDomain(), instanceId.getId());
    }

    public UserId toUserIdBoundary(String userId) {
        String[] values = userId.split(";");
        String domain = values[0];
        String email = values[1];
        return new UserId(domain, email);
    }

    public String toUserIdEntity(UserId userId) {
        return String.format("%s;%s", userId.getDomain(), userId.getEmail());
    }
}