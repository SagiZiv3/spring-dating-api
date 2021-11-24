package iob.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="INSTANCES")
public class InstanceEntity {
    @Id
    private String instanceId;
    private String type;
    private String name;
    @Getter(AccessLevel.NONE)
    private boolean active;
    @Transient
    private Date createdTimestamp;
    @Transient
    private Map<String, String> createdBy;
    private String location;
    @Transient
    private Map<String, Object> instanceAttributes;

    public boolean getActive(){
        return this.active;
    }
}