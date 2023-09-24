package ru.practicum.shareit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends RuntimeException {
    private final HttpStatus status;

    public ValidationException(String error, HttpStatus status) {
        super(error);
        this.status = status;
    }
}
