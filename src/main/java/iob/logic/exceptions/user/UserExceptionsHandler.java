package iob.logic.exceptions.user;

import iob.logic.exceptions.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class UserExceptionsHandler {
    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ExceptionResponse> handleUserNotFoundException(HttpServletRequest request, UserNotFoundException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("User with email '%s' doesn't exist in domain '%s'", e.getEmail(), e.getDomain()),
                request.getRequestURI(), HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(UserExistsException.class)
    private ResponseEntity<ExceptionResponse> handleUserExistsException(HttpServletRequest request, UserExistsException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("User with email '%s' already exist in domain '%s'", e.getEmail(), e.getDomain()),
                request.getRequestURI(), HttpStatus.CONFLICT // Source: https://stackoverflow.com/a/3826024
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}