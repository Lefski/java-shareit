package ru.practicum.shareit.user.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;

@Data
@Validated
public class UserDto {
    private int id;
    private String name;
    @Email
    private String email;

    public UserDto() {
    }
}
