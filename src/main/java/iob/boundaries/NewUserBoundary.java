package iob.boundaries;

import iob.boundaries.helpers.UserRoleBoundary;
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
    private UserRoleBoundary role;
    private String username;
    private String avatar;
}