package iob.data;


import iob.data.primarykeys.UserPrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS")
@IdClass(UserPrimaryKey.class)
public class UserEntity {
    //<editor-fold desc="Primary key">
    @Id
    @NonNull
    private String domain;
    @Id
    @NonNull
    private String email;
    //</editor-fold>

    //<editor-fold desc="Entity's attributes">
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @NonNull
    private String username;
    private String avatar;
    //</editor-fold>

    @Override
    public int hashCode() {
        return Objects.hash(domain, email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(domain, that.domain) && Objects.equals(email, that.email);
    }
}