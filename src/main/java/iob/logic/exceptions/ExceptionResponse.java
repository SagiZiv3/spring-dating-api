package iob.logic.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
public class ExceptionResponse {
    private final String error;
    private final Date timestamp;
    private final HttpStatus httpStatus;

    public ExceptionResponse(String error, HttpStatus httpStatus) {
        this.error = error;
        this.httpStatus = httpStatus;
        this.timestamp = new Date();
    }
}