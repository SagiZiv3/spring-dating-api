package iob.logic.exceptions.activity;

import lombok.Getter;

@Getter
public class UnknownActivityTypeException extends RuntimeException {
    private static final long serialVersionUID = 808145515962768936L;
    private final String type;

    public UnknownActivityTypeException(String type) {
        this.type = type;
    }
}