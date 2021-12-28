package iob.logic.exceptions.activity;


import iob.logic.exceptions.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ActivityExceptionsHandler {
    @ExceptionHandler(InvalidLikeActivityException.class)
    private ResponseEntity<ExceptionResponse> handleInvalidLikeActivityException(InvalidLikeActivityException e) {
        ExceptionResponse response = new ExceptionResponse(
                "User is not allowed to add like to himself!",
                HttpStatus.UNPROCESSABLE_ENTITY
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MultipleLoginsException.class)
    private ResponseEntity<ExceptionResponse> handleMultipleLoginsException(MultipleLoginsException e) {
        ExceptionResponse response = new ExceptionResponse(
                "The user is already logged in, multiple logins are not allowed",
                HttpStatus.FORBIDDEN
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}