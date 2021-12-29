package iob.logic.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
public class ExceptionResponse {
    private final String error;
    private final String path;
    private final Date timestamp;
    private final HttpStatus httpStatus;

    public ExceptionResponse(String error, String path, HttpStatus httpStatus) {
        this.error = error;
        this.path = path;
        this.httpStatus = httpStatus;
        this.timestamp = new Date();
    }
}