package iob.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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


@NoArgsConstructor
@AllArgsConstructor
@Data
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
    private String domain;
    //</editor-fold>

    //<editor-fold desc="Entity's attributes">
    private String type;
    private String name;
    // Cancel the default getter, because it is prefixed with "is" instead of "get"
    @Getter(AccessLevel.NONE)
    private boolean active;
    // Define that we save the data and the time
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;
    private LocationEntity location;
    private CreatedByEntity createdBy;
    // TODO: Add the conversion to CLOB
    @Transient
    private Map<String, Object> instanceAttributes;
    //</editor-fold>

    //<editor-fold desc="Many to Many relation">
    // Define a new table that would store the references
    @JoinTable(name = "INSTANCES_TO_INSTANCES",
            // The main columns for this attribute are the parent's primary key which is constructed from domain and id
            joinColumns = {@JoinColumn(name = "PARENT_ID", referencedColumnName = "id"),
                    @JoinColumn(name = "PARENT_DOMAIN", referencedColumnName = "domain")},

            // The referenced columns for this attribute are the child's primary key which is also constructed from domain and id
            inverseJoinColumns = {@JoinColumn(name = "CHILD_ID", referencedColumnName = "id"),
                    @JoinColumn(name = "CHILD_DOMAIN", referencedColumnName = "domain")})
    // Declare the parent's side of the relation
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<InstanceEntity> parentInstances = new HashSet<>();

    // Declare the children's size of the relation, and define that it is related to the parentInstances
    @ManyToMany(mappedBy = "parentInstances", fetch = FetchType.LAZY)
    private Set<InstanceEntity> childInstances = new HashSet<>();
    //</editor-fold>

    public boolean getActive() {
        return this.active;
    }
}