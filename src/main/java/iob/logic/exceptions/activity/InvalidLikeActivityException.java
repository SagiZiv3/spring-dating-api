package iob.logic.exceptions.activity;

import lombok.Getter;

public class InvalidLikeActivityException extends RuntimeException {
    private static final long serialVersionUID = 808145515962768934L;
    @Getter
    private final String userEmail, userDomain;

    public InvalidLikeActivityException(String userEmail, String userDomain) {
        super();
        this.userEmail = userEmail;
        this.userDomain = userDomain;
    }

    public InvalidLikeActivityException(Throwable throwable, String userEmail, String userDomain) {
        super(throwable);
        this.userEmail = userEmail;
        this.userDomain = userDomain;
    }

}