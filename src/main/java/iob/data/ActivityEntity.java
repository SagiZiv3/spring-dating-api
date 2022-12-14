package iob.data;

import iob.data.converters.MapToStringConverter;
import iob.data.primarykeys.ActivityPrimaryKey;
import iob.data.primarykeys.InstancePrimaryKey;
import iob.data.primarykeys.UserPrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Map;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ACTIVITIES")
@IdClass(ActivityPrimaryKey.class)
public class ActivityEntity {
    //<editor-fold desc="Primary key">
    @Id
    // The index would be generated from a sequence named "ACTIVITIES_SEQUENCE"
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVITIES_SEQUENCE")
    // Here we create that sequence, and define it to be updated every insertion (allocationSize = 1).
    @SequenceGenerator(name = "ACTIVITIES_SEQUENCE", sequenceName = "ACTIVITIES_SEQUENCE", allocationSize = 1)
    private long id;
    @Id
    @NonNull
    private String domain;
    //</editor-fold>

    //<editor-fold desc="Entity's attributes">
    @NonNull
    private String type;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "INSTANCE_ID")),
            @AttributeOverride(name = "domain", column = @Column(name = "INSTANCE_DOMAIN"))
    })
    private InstancePrimaryKey instance;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "email", column = @Column(name = "INVOKED_BY_EMAIL")),
            @AttributeOverride(name = "domain", column = @Column(name = "INVOKED_BY_DOMAIN"))
    })
    private UserPrimaryKey invokedBy;
    @Lob
    @Convert(converter = MapToStringConverter.class)
    private Map<String, Object> activityAttributes;
    //</editor-fold>

    @Override
    public int hashCode() {
        return Objects.hash(id, domain);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityEntity entity = (ActivityEntity) o;
        return id == entity.id && domain.equals(entity.domain);
    }
}