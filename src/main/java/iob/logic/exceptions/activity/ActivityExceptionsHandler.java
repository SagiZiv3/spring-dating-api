package iob.logic.exceptions.activity;


import iob.logic.exceptions.ExceptionResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@ControllerAdvice
public class ActivityExceptionsHandler {
    @ExceptionHandler(InvalidLikeActivityException.class)
    private ResponseEntity<ExceptionResponse> handleInvalidLikeActivityException(HttpServletRequest request, InvalidLikeActivityException e) {
        ExceptionResponse response = new ExceptionResponse(
                "User is not allowed to add like to himself!",
                request.getRequestURI(), HttpStatus.UNPROCESSABLE_ENTITY
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MultipleLoginsException.class)
    private ResponseEntity<ExceptionResponse> handleMultipleLoginsException(HttpServletRequest request, MultipleLoginsException e) {
        ExceptionResponse response = new ExceptionResponse(
                "The user is already logged in, multiple logins are yet to be supported.",
                request.getRequestURI(),
                HttpStatus.CONFLICT
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MultipleSignupsException.class)
    private ResponseEntity<ExceptionResponse> handleMultipleSignupsException(HttpServletRequest request, MultipleSignupsException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("User with email %s in domain %s is already signed to the system",
                        e.getUserEmail(), e.getUserDomain()),
                request.getRequestURI(),
                HttpStatus.CONFLICT
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MissingLoginInstanceException.class)
    private ResponseEntity<ExceptionResponse> handleMissingLoginInstanceException(HttpServletRequest request, MissingLoginInstanceException e) {
        ExceptionResponse response = new ExceptionResponse(
                "The user is already logged out of the system.",
                request.getRequestURI(), HttpStatus.CONFLICT
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(BrokenUserInstanceException.class)
    private ResponseEntity<ExceptionResponse> handleBrokenUserInstanceException(HttpServletRequest request, BrokenUserInstanceException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("The user instance for user %s in domain %s doesn't contain the following attributes: %s",
                        e.getUserEmail(), e.getUserDomain(), e.getMissingAttributes()),
                request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MissingUserInstanceException.class)
    private ResponseEntity<ExceptionResponse> handleMissingUserInstanceException(HttpServletRequest request, MissingUserInstanceException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("The user instance for user %s in domain %s doesn't exist.",
                        e.getUserEmail(), e.getUserDomain()),
                request.getRequestURI(), HttpStatus.NOT_FOUND
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @ExceptionHandler(UnknownActivityTypeException.class)
    private ResponseEntity<ExceptionResponse> handleUnknownActivityTypeException(HttpServletRequest request, UnknownActivityTypeException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("Unknown activity type: '%s', available types are: (%s)",
                        e.getType(),
                        e.getAvailableTypes()
                                .stream()
                                .sorted()
                                .map(str -> StringUtils.wrap(str, "'"))
                                .collect(Collectors.joining(", "))
                ),
                request.getRequestURI(), HttpStatus.UNPROCESSABLE_ENTITY
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}