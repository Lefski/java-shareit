package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;

    public ItemDto() {

    }

    public ItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }


    public ItemDto(String name, String description) {
        this.name = name;
        this.description = description;
    }


    public ItemDto(String name, String description, Boolean available, Integer requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }

    public ItemDto(int id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
