package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException {
    private HttpStatus status;

    public NotFoundException(String error) {
        super(error);
    }

    public NotFoundException(String error, HttpStatus status) {
        super(error);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
