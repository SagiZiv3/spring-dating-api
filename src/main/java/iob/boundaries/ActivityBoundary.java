package iob.boundaries;

import iob.boundaries.helpers.ActivityIdBoundary;
import iob.boundaries.helpers.InstanceIdWrapper;
import iob.boundaries.helpers.InvokedByBoundary;
import lombok.*;

import java.util.Date;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class ActivityBoundary {
    private ActivityIdBoundary activityId;
    private String type;
    private InstanceIdWrapper instance;
    private Date createdTimestamp;
    private InvokedByBoundary invokedBy;
    private Map<String, Object> activityAttributes;
}