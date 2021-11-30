package iob.boundaries;

import iob.boundaries.helpers.InitiatedBy;
import iob.boundaries.helpers.Location;
import iob.boundaries.helpers.ObjectId;
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
    private InitiatedBy createdBy;
    private Location location;
    private Map<String, Object> instanceAttributes;
}