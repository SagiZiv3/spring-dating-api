package iob.logic.exceptions.instance;

public class InvalidBindingOperationException extends RuntimeException {
    public InvalidBindingOperationException() {
    }

    public InvalidBindingOperationException(String s) {
        super(s);
    }

    public InvalidBindingOperationException(Throwable throwable) {
        super(throwable);
    }

    public InvalidBindingOperationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}