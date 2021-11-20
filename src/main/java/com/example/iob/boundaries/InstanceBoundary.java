package com.example.iob.boundaries;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InstanceBoundary {
    private ObjectId instanceId;
    private String type;
    private String name;
    private Boolean active;
    private Date createdTimestamp;
    private Map<String, UserId> createdBy;
    private Location location;
    private Map<String, Object> instanceAttributes;

    public InstanceBoundary(InstanceBoundary instance) {
        setActive(instance.active);
        setInstanceAttributes(instance.instanceAttributes);
        setInstanceId(instance.instanceId);
        setCreatedBy(instance.createdBy);
        setLocation(instance.location);
        setCreatedTimestamp(instance.createdTimestamp);
        setType(instance.type);
        setName(instance.name);
    }

}