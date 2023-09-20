package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class User {
    private int id;
    private String name;
    @Email
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
