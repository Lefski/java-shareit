package ru.practicum.shareit.user.model;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private final String error;
    private HttpStatus status;

    public ErrorResponse(String error, HttpStatus status) {
        this.error = error;
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
