package com.example.demo.boundaries;

import java.util.Date;
import java.util.Map;

public class InstanceBoundary {
    private static long id;
    private Map<String, String> instanceId;
    private String type;
    private String name;
    private Boolean active;
    private Date createdTimestamp;
    private Map<String, Map<String, String>> createdBy;
    private Map<String, Double> location;
    private Map<String, Object> instanceAttributes;

    public InstanceBoundary() {
    }

    public InstanceBoundary(InstanceBoundary instance) {
        id++;
        this.name = instance.name;
        this.type = instance.type;
        this.active = instance.active;
        this.createdTimestamp = new Date();
        this.instanceId = instance.instanceId;
        this.createdBy = instance.createdBy;
        this.location = instance.location;
        this.instanceAttributes = instance.instanceAttributes;
    }

    public static long getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean isActive() {
        return this.active;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Map<String, String> getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(Map<String, String> instanceId) {
        this.instanceId = instanceId;
    }

    public Map<String, Map<String, String>> getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Map<String, Map<String, String>> createdBy) {
        this.createdBy = createdBy;
    }

    public Map<String, Double> getLocation() {
        return this.location;
    }

    public void setLocation(Map<String, Double> location) {
        this.location = location;
    }

    public Map<String, Object> getInstanceAttributes() {
        return this.instanceAttributes;
    }

    public void setInstanceAttributes(Map<String, Object> instanceAttributes) {
        this.instanceAttributes = instanceAttributes;
    }

}
