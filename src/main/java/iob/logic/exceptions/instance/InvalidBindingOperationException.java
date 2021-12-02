package iob.logic.exceptions.instance;

public class InvalidBindingOperationException extends RuntimeException {

    public InvalidBindingOperationException(String s) {
        super(s);
    }

    public InvalidBindingOperationException(Throwable throwable) {
        super(throwable);
    }
}