package iob.logic;

import iob.boundaries.UserBoundary;
import iob.logic.annotations.UserRoleParameter;
import iob.logic.exceptions.UserPermissionException;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class UserPermissionsHandler {
    private final UsersService usersService;

    @Autowired
    public UserPermissionsHandler(UsersService usersService) {
        this.usersService = usersService;
    }

    public void throwIfNotAuthorized(String userDomain, String userEmail, UserRoleParameter... permittedRoles) {
        UserBoundary user = usersService.login(userDomain, userEmail);
        UserRoleParameter passedUserRole = UserRoleParameter.valueOf(user.getRole().name().toUpperCase());

        // Throw exception if the user's role is not in the permitted roles list.
        if (Arrays.stream(permittedRoles).noneMatch(passedUserRole::equals)) {
            // Convert the permitted roles array to a string separated by commas.
            String permittedRolesString = rolesToString(permittedRoles);

            throw new UserPermissionException(WordUtils.capitalizeFully(passedUserRole.name().toLowerCase()),
                    permittedRolesString, user.getUserId().getEmail(), user.getUserId().getDomain());
        }
    }

    public Collection<Boolean> getAllowedActiveStatesForUser(String userDomain, String userEmail) {
        UserBoundary userBoundary = usersService.login(userDomain, userEmail);
        switch (userBoundary.getRole()) {
            case PLAYER:
                // Player can only get active instances
                return Collections.singleton(true);
            case MANAGER:
                // Manager can get either active instances or inactive instances.
                return Arrays.asList(true, false);
            default:
                throw new UserPermissionException(WordUtils.capitalizeFully(userBoundary.getRole().name()),
                        rolesToString(UserRoleParameter.MANAGER, UserRoleParameter.PLAYER), userEmail, userDomain);
        }
    }

    private static String rolesToString(UserRoleParameter... permittedRoles) {
        return Arrays.stream(permittedRoles)
                // Get the name of every role.
                .map(UserRoleParameter::toString)
                // Sort the collection to make all the exceptions unified (with same order of roles)
                .sorted()
                .collect(Collectors.joining(", "));
    }
}