package iob.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GeneralExceptionsHandler {
    @ExceptionHandler(InvalidInputException.class)
    private ResponseEntity<ExceptionResponse> handleInvalidInputException(HttpServletRequest request, InvalidInputException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("The value '%s' in field '%s' is invalid", e.getFieldValue(), e.getFieldName()),
                request.getRequestURI(), HttpStatus.UNPROCESSABLE_ENTITY // Source: https://stackoverflow.com/a/42171674
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(UserPermissionException.class)
    private ResponseEntity<ExceptionResponse> handleUserPermissionException(HttpServletRequest request, UserPermissionException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("User with email '%s' in domain '%s' with role '%s' can't invoke required method, permitted roles are %s",
                        e.getUserEmail(), e.getUserDomain(), e.getUserRole(), e.getPermittedRoles()),
                request.getRequestURI(), HttpStatus.FORBIDDEN // Source: https://stackoverflow.com/a/6937030
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}