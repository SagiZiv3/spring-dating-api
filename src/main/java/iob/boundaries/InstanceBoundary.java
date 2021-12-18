package iob.boundaries;

import iob.boundaries.helpers.CreatedByBoundary;
import iob.boundaries.helpers.InstanceIdBoundary;
import iob.boundaries.helpers.Location;
import lombok.*;

import java.util.Date;
import java.util.Map;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceBoundary that = (InstanceBoundary) o;
        return instanceId.equals(that.instanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId);
    }
}