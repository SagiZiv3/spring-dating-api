package com.example.demo.boundaries;

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
public class ActivityBoundary {
    private static long id;
    private ObjectId activityId;
    private String type;
    private Map<String, ObjectId> instance;
    private Date createdTimestamp;
    private Map<String, UserId> invokedBy;
    private Map<String, Object> activityAttributes;
}