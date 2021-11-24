package iob.boundaries;

import iob.boundaries.helpers.InitiatedBy;
import iob.boundaries.helpers.Instance;
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
public class ActivityBoundary {
    private ObjectId activityId;
    private String type;
    private Instance instance;
    private Date createdTimestamp;
    private InitiatedBy invokedBy;
    private Map<String, Object> activityAttributes;
}