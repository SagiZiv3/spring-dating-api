package iob;

import iob.boundaries.helpers.UserRoleBoundary;
import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN(UserRoleBoundary.ADMIN, "UNITTESTS_ADMIN@shahar.co.il"),
    MANAGER(UserRoleBoundary.MANAGER, "UNITTESTS_MANAGER@shahar.co.il"),
    PLAYER(UserRoleBoundary.PLAYER, "UNITTESTS_PLAYER@shahar.co.il");

    private final UserRoleBoundary userRoleBoundary;
    private final String email;

    UserRole(UserRoleBoundary userRoleBoundary, String email) {
        this.userRoleBoundary = userRoleBoundary;
        this.email = email;
    }
}