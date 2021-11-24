package iob.boundaries;

import iob.boundaries.helpers.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewUserBoundary {
    private String email;
    private UserRole role;
    private String username;
    private String avatar;
}