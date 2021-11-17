package com.example.demo.boundaries;

import java.util.Date;
import java.util.Map;


@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.ToString
public class InstanceBoundary {
    @lombok.Getter
    private static long id;
    private Map<String, String> instanceId;
    private String type;
    private String name;
    private Boolean active;
    private Date createdTimestamp;
    private Map<String, Map<String, String>> createdBy;
    private Map<String, Double> location;
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
