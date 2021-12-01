package iob.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralExceptionsHandler {
    @ExceptionHandler(InvalidInputException.class)
    private ResponseEntity<ExceptionResponse> handleInvalidInputException(InvalidInputException e) {
        ExceptionResponse response = new ExceptionResponse(
                String.format("The value '%s' in field '%s' is invalid", e.getFieldValue(), e.getFieldName()),
                HttpStatus.UNPROCESSABLE_ENTITY // Source: https://stackoverflow.com/a/42171674
        );
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}