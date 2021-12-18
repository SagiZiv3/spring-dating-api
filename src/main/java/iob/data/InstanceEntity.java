package iob.data;

import iob.data.converters.MapToStringConverter;
import iob.data.embeddedentities.LocationEntity;
import iob.data.primarykeys.InstancePrimaryKey;
import iob.logic.exceptions.instance.InvalidBindingOperationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "INSTANCES")
@IdClass(InstancePrimaryKey.class)
public class InstanceEntity {
    //<editor-fold desc="Primary key">
    @Id
    // The index would be generated from a sequence named "INSTANCES_SEQUENCE"
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INSTANCES_SEQUENCE")
    // Here we create that sequence, and define it to be updated every insertion (allocationSize = 1).
    @SequenceGenerator(name = "INSTANCES_SEQUENCE", sequenceName = "INSTANCES_SEQUENCE", allocationSize = 1)
    private long id;
    @Id
    @NonNull
    private String domain;
    //</editor-fold>

    //<editor-fold desc="Entity's attributes">
    @NonNull
    private String type;
    @NonNull
    private String name;
    private boolean active;
    // Define that we save the data and the time
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;
    @Embedded
    private LocationEntity location = new LocationEntity();
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private UserEntity createdBy;
    @Lob
    @Convert(converter = MapToStringConverter.class)
    private Map<String, Object> instanceAttributes;
    //</editor-fold>

    //<editor-fold desc="Many to Many relation">
    // Define a new table that would store the references
    @JoinTable(name = "INSTANCES_TO_INSTANCES",
            // The main columns for this attribute are the parent's primary key which is constructed from domain and id
            inverseJoinColumns = {@JoinColumn(name = "PARENT_ID", referencedColumnName = "id"),
                    @JoinColumn(name = "PARENT_DOMAIN", referencedColumnName = "domain")},

            // The referenced columns for this attribute are the child's primary key which is also constructed from domain and id
            joinColumns = {@JoinColumn(name = "CHILD_ID", referencedColumnName = "id"),
                    @JoinColumn(name = "CHILD_DOMAIN", referencedColumnName = "domain")})
    // Declare the parent's side of the relation
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<InstanceEntity> parentInstances = new HashSet<>();

    // Declare the children's size of the relation, and define that it is related to the parentInstances
    @ManyToMany(mappedBy = "parentInstances", fetch = FetchType.LAZY)
    private Set<InstanceEntity> childInstances = new HashSet<>();
    //</editor-fold>

    public void addParent(InstanceEntity parent) {
        if (this.equals(parent))
            throw new InvalidBindingOperationException("Cannot assign parent to himself");
        parentInstances.add(parent);
    }

    public void addChild(InstanceEntity child) {
        if (this.equals(child))
            throw new InvalidBindingOperationException("Cannot assign child to himself");
        childInstances.add(child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, domain);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceEntity entity = (InstanceEntity) o;
        return id == entity.id && domain.equals(entity.domain);
    }
}