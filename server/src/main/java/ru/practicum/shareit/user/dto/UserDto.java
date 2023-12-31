package ru.practicum.shareit.user.dto;

import lombok.Data;


@Data
public class UserDto {
    private int id;
    private String name;
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
