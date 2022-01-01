package iob.logic.exceptions.activity;

import lombok.Getter;

@Getter
public class MultipleSignupsException extends RuntimeException {
    private static final long serialVersionUID = 808145515962768935L;
    private final String userEmail, userDomain;

    public MultipleSignupsException(String userEmail, String userDomain) {
        this.userEmail = userEmail;
        this.userDomain = userDomain;
    }
}