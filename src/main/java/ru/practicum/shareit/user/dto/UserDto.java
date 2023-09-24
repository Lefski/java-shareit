package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDto {
    private int id;
    private String name;
    @Email
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserDto(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public UserDto() {
    }
}
