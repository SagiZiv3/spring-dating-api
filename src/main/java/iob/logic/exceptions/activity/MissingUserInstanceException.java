package iob.logic.exceptions.activity;

import lombok.Getter;

@Getter
public class MissingUserInstanceException extends RuntimeException {
    private static final long serialVersionUID = 808145515962768932L;
    private final String userEmail, userDomain;

    public MissingUserInstanceException(String userEmail, String userDomain) {
        this.userEmail = userEmail;
        this.userDomain = userDomain;
    }
}