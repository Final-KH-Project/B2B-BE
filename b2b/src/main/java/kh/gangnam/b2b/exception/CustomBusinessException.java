package kh.gangnam.b2b.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public abstract class CustomBusinessException extends RuntimeException {

    private final HttpStatus httpStatus;

    public CustomBusinessException(String message, HttpStatus status) {
        super(message);
        this.httpStatus = status;
    }

}