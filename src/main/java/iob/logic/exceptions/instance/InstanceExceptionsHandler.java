package iob.logic.exceptions.instance;

import iob.logic.exceptions.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InstanceExceptionsHandler {
    @ExceptionHandler(InstanceNotFoundException.class)
    private ResponseEntity<ExceptionResponse> handleInstanceNotFoundException(InstanceNotFoundException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("Instance with id '%s' doesn't exist in domain '%s'", e.getId(), e.getDomain()),
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(InvalidBindingOperationException.class)
    private ResponseEntity<ExceptionResponse> handleInvalidBindingOperationException(InvalidBindingOperationException e) {
        ExceptionResponse response = new ExceptionResponse(
                (e.getMessage() == null || e.getMessage().isEmpty()) ?
                        "Can't bind instance to himself" : e.getMessage(),
                HttpStatus.UNPROCESSABLE_ENTITY
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}