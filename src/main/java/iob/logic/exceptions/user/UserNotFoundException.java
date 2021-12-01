package iob.logic.exceptions.user;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 808145515962768921L;
    private final String domain, email;

    public UserNotFoundException(String domain, String email) {
        super();
        this.domain = domain;
        this.email = email;
    }

    public UserNotFoundException(Throwable throwable, String domain, String email) {
        super(throwable);
        this.domain = domain;
        this.email = email;
    }
}