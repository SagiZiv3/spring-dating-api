package iob.boundaries.converters;

import iob.boundaries.helpers.InitiatedBy;
import iob.boundaries.helpers.ObjectId;
import iob.boundaries.helpers.UserId;
import iob.data.InstancePrimaryKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IdsConverter {
    @Value("${application.entity.delimiter}")
    private String delimiter;

    public Map<String, String> toUserIdMapEntity(InitiatedBy createdBy) {
        Map<String, String> entity = new HashMap<>();
        entity.put("userId", createdBy.getUserId().getDomain() + delimiter + createdBy.getUserId().getEmail());
        return entity;
    }

    public InitiatedBy toUserIdMapBoundary(Map<String, String> createdBy) {
        if (createdBy == null) return null;
        String[] parameters = createdBy.get("userId").split(delimiter);
        return new InitiatedBy(new UserId(parameters[0], parameters[1]));
    }

    public ObjectId toObjectIdBoundary(InstancePrimaryKey instanceId) {
        if (instanceId == null) return null;
        return new ObjectId(instanceId.getDomain(), Long.toString(instanceId.getId()));
    }

    public String toObjectIdEntity(ObjectId instanceId) {
        if (instanceId == null) return null;
        return String.format("%s%s%s", instanceId.getDomain(), delimiter, instanceId.getId());
    }

    public UserId toUserIdBoundary(String userId) {
        if (userId == null) return null;
        String[] values = userId.split(delimiter);
        String domain = values[0];
        String email = values[1];
        return new UserId(domain, email);
    }

    public String toUserIdEntity(UserId userId) {
        if (userId == null) return null;
        return String.format("%s%s%s", userId.getDomain(), delimiter, userId.getEmail());
    }
}