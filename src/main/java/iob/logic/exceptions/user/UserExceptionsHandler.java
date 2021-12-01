package iob.logic.exceptions.user;

import iob.logic.exceptions.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionsHandler {
    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("User with email '%s' doesn't exist in domain '%s'", e.getEmail(), e.getDomain()),
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(UserExistsException.class)
    private ResponseEntity<ExceptionResponse> handleUserExistsException(UserExistsException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("User with email '%s' already exist in domain '%s'", e.getEmail(), e.getDomain()),
                HttpStatus.CONFLICT // Source: https://stackoverflow.com/a/3826024
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(UserPermissionException.class)
    private ResponseEntity<ExceptionResponse> handleUserPermissionException(UserPermissionException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("User with email '%s' in domain '%s' with role '%s' can't access required resource, required role is %s",
                        e.getUserEmail(), e.getUserDomain(), e.getUserRole(), e.getRequiredRole()),
                HttpStatus.UNAUTHORIZED
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}