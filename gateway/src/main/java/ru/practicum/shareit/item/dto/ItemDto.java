package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.dto.ItemRequestDto;


@Data
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
    private ItemRequestDto request;

    public ItemDto() {

    }

}
