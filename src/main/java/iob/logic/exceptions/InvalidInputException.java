package iob.logic.exceptions;

import lombok.Getter;

@Getter
public class InvalidInputException extends RuntimeException {
    private static final long serialVersionUID = 808145515962768923L;
    private final String fieldName, fieldValue;

    public InvalidInputException(String fieldName, String fieldValue) {
        super();
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public InvalidInputException(Throwable throwable, String fieldName, String fieldValue) {
        super(throwable);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}