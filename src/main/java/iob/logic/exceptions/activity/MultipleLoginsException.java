package iob.logic.exceptions.activity;

public class MultipleLoginsException extends RuntimeException {
    public MultipleLoginsException() {
    }

    public MultipleLoginsException(Throwable throwable) {
        super(throwable);
    }
}