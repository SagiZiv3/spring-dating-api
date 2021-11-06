package com.example.demo.boundaries;

import java.util.Date;
import java.util.Map;

public class ActivityBoundary {
    private static long id;
    private Map<String, String> activityId;
    private String type;
    private Map<String, Map<String, String>> instance;
    private Date createdTimestamp;
    private Map<String, Map<String, String>> invokedBy;
    private Map<String, Object> activityAttributes;

    public ActivityBoundary() {
    }

    public ActivityBoundary(Map<String, String> activityId, String type, Map<String, Map<String, String>> instance,
            Date createdTimestamp, Map<String, Map<String, String>> invokedBy, Map<String, Object> activityAttributes) {
        this.activityId = activityId;
        this.type = type;
        this.instance = instance;
        this.createdTimestamp = createdTimestamp;
        this.invokedBy = invokedBy;
        this.activityAttributes = activityAttributes;
    }

    public Map<String, String> getActivityId() {
        return this.activityId;
    }

    public void setActivityId(Map<String, String> activityId) {
        this.activityId = activityId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Map<String, String>> getInstance() {
        return this.instance;
    }

    public void setInstance(Map<String, Map<String, String>> instance) {
        this.instance = instance;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Map<String, Map<String, String>> getInvokedBy() {
        return this.invokedBy;
    }

    public void setInvokedBy(Map<String, Map<String, String>> invokedBy) {
        this.invokedBy = invokedBy;
    }

    public Map<String, Object> getActivityAttributes() {
        return this.activityAttributes;
    }

    public void setActivityAttributes(Map<String, Object> activityAttributes) {
        this.activityAttributes = activityAttributes;
    }
}
