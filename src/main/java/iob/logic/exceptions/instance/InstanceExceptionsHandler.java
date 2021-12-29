package iob.logic.exceptions.instance;

import iob.logic.exceptions.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class InstanceExceptionsHandler {
    @ExceptionHandler(InstanceNotFoundException.class)
    private ResponseEntity<ExceptionResponse> handleInstanceNotFoundException(HttpServletRequest request, InstanceNotFoundException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("Instance with id '%s' doesn't exist in domain '%s'", e.getId(), e.getDomain()),
                request.getRequestURI(), HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(InvalidBindingOperationException.class)
    private ResponseEntity<ExceptionResponse> handleInvalidBindingOperationException(HttpServletRequest request, InvalidBindingOperationException e) {
        ExceptionResponse response = new ExceptionResponse(
                (e.getMessage() == null || e.getMessage().isEmpty()) ?
                        "Can't bind instance to himself" : e.getMessage(),
                request.getRequestURI(), HttpStatus.UNPROCESSABLE_ENTITY
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}