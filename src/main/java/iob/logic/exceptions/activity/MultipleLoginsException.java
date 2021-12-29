package iob.logic.exceptions.activity;

public class MultipleLoginsException extends RuntimeException {
    private static final long serialVersionUID = 808145515962768931L;
    public MultipleLoginsException() {
    }

    public MultipleLoginsException(Throwable throwable) {
        super(throwable);
    }
}