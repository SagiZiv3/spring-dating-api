package iob.logic.exceptions.user;

import lombok.Getter;

@Getter
public class UserExistsException extends RuntimeException {
    private static final long serialVersionUID = 808145515962768922L;
    private final String domain, email;

    public UserExistsException(String domain, String email) {
        super();
        this.domain = domain;
        this.email = email;
    }

    public UserExistsException(Throwable throwable, String domain, String email) {
        super(throwable);
        this.domain = domain;
        this.email = email;
    }
}