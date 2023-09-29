package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Data
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
    private List<Comment> comments;
    private ItemRequestDto request;

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


    public ItemDto(int id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
