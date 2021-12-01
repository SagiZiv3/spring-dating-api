package iob.logic.exceptions.user;

import lombok.Getter;

@Getter
public class UserPermissionException extends RuntimeException {
    private static final long serialVersionUID = 808145515962768924L;
    private final String userRole, requiredRole;
    private final String userEmail, userDomain;

    public UserPermissionException(String userRole, String requiredRole, String userEmail, String userDomain) {
        super();
        this.userRole = userRole;
        this.requiredRole = requiredRole;
        this.userEmail = userEmail;
        this.userDomain = userDomain;
    }

    public UserPermissionException(Throwable throwable, String userRole, String requiredRole, String userEmail, String userDomain) {
        super(throwable);
        this.userRole = userRole;
        this.requiredRole = requiredRole;
        this.userEmail = userEmail;
        this.userDomain = userDomain;
    }
}