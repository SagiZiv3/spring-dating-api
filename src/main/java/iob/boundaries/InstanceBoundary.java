package iob.boundaries;

import iob.boundaries.helpers.CreatedByBoundary;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.boundaries.helpers.Location;
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
    private InstanceIdBoundary instanceId;
    private String type;
    private String name;
    private Boolean active;
    private Date createdTimestamp;
    private CreatedByBoundary createdBy;
    private Location location;
    private Map<String, Object> instanceAttributes;
}