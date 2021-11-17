package com.example.demo.data;

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
public class InstanceEntity {
    private String instanceId;
    private String type;
    private String name;
    private boolean active;
    private Date createdTimestamp;
    private String createdBy;
    private String location;
    private Map<String, Object> instanceAttributes;
}