package iob.data;


import iob.data.primarykeys.UserPrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS")
@IdClass(UserPrimaryKey.class)
public class UserEntity {
    @Id
    @NonNull
    private String domain;
    @Id
    @NonNull
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @NonNull
    private String username;
    private String avatar;
}