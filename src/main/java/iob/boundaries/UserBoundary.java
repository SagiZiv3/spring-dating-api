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

    private Integer heightInCm;
    private String languages;
    private String lookingFor; // Maybe change to enum, options: Long/Short term, One time
    private String interestedIn; // Male / Female / Both / None
    private String about;
    private String gender; // Male / Female / All / None
    private String attractedTo; // Straight / Bi / Homo / None
    private String relationshipType; // Monogamous / Polygamous / Hookups / None

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