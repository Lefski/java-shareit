package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable()

        );
    }

    public static ItemDtoWithBookings toItemDtoWithBookings(Item item) {
        return new ItemDtoWithBookings(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
    }
}
