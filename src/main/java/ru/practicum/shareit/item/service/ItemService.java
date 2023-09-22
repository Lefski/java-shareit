package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Integer ownerId);

    ItemDto editItem(Integer itemId, ItemDto itemDto, Integer ownerId);

    ItemDtoWithBookings getItemById(Integer itemId, Integer userId);

    CommentDto addCommentToItem(CommentDto commentDto);

    List<ItemDtoWithBookings> getAllItemsByOwner(Integer ownerId);

    List<ItemDto> searchItems(String text);
}
