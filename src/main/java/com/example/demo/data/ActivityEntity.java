package com.example.demo.data;

import com.example.demo.boundaries.ObjectId;
import com.example.demo.boundaries.UserId;
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
public class ActivityEntity {
    private String activityId;
    private String type;
    private Map<String, ObjectId> instance;
    private Date createdTimestamp;
    private Map<String, UserId> invokedBy;
    private Map<String, Object> activityAttributes;
}