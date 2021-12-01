package iob.boundaries;

import iob.boundaries.helpers.ActivityIdBoundary;
import iob.boundaries.helpers.InstanceIdWrapper;
import iob.boundaries.helpers.InvokedByBoundary;
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
    private ActivityIdBoundary activityId;
    private String type;
    private InstanceIdWrapper instance;
    private Date createdTimestamp;
    private InvokedByBoundary invokedBy;
    private Map<String, Object> activityAttributes;
}