package iob.boundaries;

import iob.boundaries.helpers.UserIdBoundary;
import iob.boundaries.helpers.UserRoleBoundary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserBoundary {
    private UserIdBoundary userId;
    private UserRoleBoundary role;
    private String username;
    private String avatar;

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBoundary that = (UserBoundary) o;
        return userId.equals(that.userId);
    }
}