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
                "The user is already logged in, multiple logins are yet to be supported.",
                HttpStatus.CONFLICT
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MissingLoginInstanceException.class)
    private ResponseEntity<ExceptionResponse> handleMissingLoginInstanceException(MissingLoginInstanceException e) {
        ExceptionResponse response = new ExceptionResponse(
                "The user is already logged out of the system.",
                HttpStatus.CONFLICT
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(BrokenUserInstanceException.class)
    private ResponseEntity<ExceptionResponse> handleBrokenUserInstanceException(BrokenUserInstanceException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("The user instance for user %s in domain %s doesn't contain the following attributes: %s",
                        e.getUserEmail(), e.getUserDomain(), e.getMissingAttributes()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MissingUserInstanceException.class)
    private ResponseEntity<ExceptionResponse> handleMissingUserInstanceException(MissingUserInstanceException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("The user instance for user %s in domain %s doesn't exist.",
                        e.getUserEmail(), e.getUserDomain()),
                HttpStatus.NOT_FOUND
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}