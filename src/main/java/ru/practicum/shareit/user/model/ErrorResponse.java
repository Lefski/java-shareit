package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResponse {
    private String error;
    private HttpStatus status;

    public ErrorResponse(String error, HttpStatus status) {
        this.error = error;
        this.status = status;
    }
}
