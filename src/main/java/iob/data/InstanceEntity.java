package iob.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "INSTANCES")
@IdClass(InstancePrimaryKey.class)
public class InstanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INSTANCES_SEQUENCE")
    @SequenceGenerator(name = "INSTANCES_SEQUENCE", sequenceName = "INSTANCES_SEQUENCE", allocationSize = 1)
    private long id;
    @Id
    private String domain;
    private String type;
    private String name;
    @Getter(AccessLevel.NONE)
    private boolean active;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;
    @Transient
    private Map<String, String> createdBy;
    private String location;
    @Transient
    private Map<String, Object> instanceAttributes;
    @JoinTable(name = "CONNECTIONS",
            joinColumns = {@JoinColumn(name = "PARENT_ID", referencedColumnName = "id"), @JoinColumn(name = "PARENT_DOMAIN", referencedColumnName = "domain")},
            inverseJoinColumns = {@JoinColumn(name = "CHILD_ID", referencedColumnName = "id"), @JoinColumn(name = "CHILD_DOMAIN", referencedColumnName = "domain")})
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<InstanceEntity> parentInstances = new HashSet<>();
    @ManyToMany(mappedBy = "parentInstances", fetch = FetchType.LAZY)
    private Set<InstanceEntity> childInstances = new HashSet<>();

    public boolean getActive() {
        return this.active;
    }
}