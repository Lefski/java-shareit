package ru.practicum.shareit.user.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Validated
public class UserDto {
    private int id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;

    public UserDto() {
    }
}
