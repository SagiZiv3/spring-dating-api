package iob.logic.exceptions.activity;

import lombok.Getter;

@Getter
public class BrokenUserInstanceException extends RuntimeException {
    private static final long serialVersionUID = 808145515962768935L;
    private final String userEmail, userDomain;
    private final String missingAttributes;

    public BrokenUserInstanceException(String userEmail, String userDomain, String... missingAttributes) {
        this.userEmail = userEmail;
        this.userDomain = userDomain;
        this.missingAttributes = String.join(", ", missingAttributes);
    }
}