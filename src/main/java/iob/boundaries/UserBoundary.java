package iob.boundaries;

import iob.boundaries.helpers.UserIdBoundary;
import iob.boundaries.helpers.UserRoleBoundary;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class UserBoundary {
    private UserIdBoundary userId;
    private UserRoleBoundary role;
    private String username;
    private String avatar;

}