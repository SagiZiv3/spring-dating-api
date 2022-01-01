package iob.logic.exceptions.activity;

import lombok.Getter;

import java.util.Collection;

@Getter
public class UnknownActivityTypeException extends RuntimeException {
    private static final long serialVersionUID = 808145515962768936L;
    private final String type;
    private final Collection<String> availableTypes;

    public UnknownActivityTypeException(String type, Collection<String> availableTypes) {
        this.type = type;
        this.availableTypes = availableTypes;
    }
}