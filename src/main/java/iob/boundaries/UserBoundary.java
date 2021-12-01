package iob.boundaries;

import iob.boundaries.helpers.UserId;
import iob.boundaries.helpers.UserRoleBoundary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserBoundary {
    private UserId userId;
    private UserRoleBoundary role;
    private String username;
    private String avatar;

}