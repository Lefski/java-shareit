package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;


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
}
