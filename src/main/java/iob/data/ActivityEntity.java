package iob.data;

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
    private Map<String, String> instance;
    private Date createdTimestamp;
    private Map<String, String> invokedBy;
    private Map<String, Object> activityAttributes;
}