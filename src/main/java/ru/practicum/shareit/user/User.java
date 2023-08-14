package ru.practicum.shareit.user;

import lombok.Data;

@Data
public class User {
    private int id;
    private String name;
    private String email;// TODO: сделать проверку на уникальность при создании

}
