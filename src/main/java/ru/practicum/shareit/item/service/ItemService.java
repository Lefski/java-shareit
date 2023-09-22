package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(ItemDto itemDto, Integer ownerId);

    Item editItem(Integer itemId, ItemDto itemDto, Integer ownerId);

    Item getItemById(Integer itemId);

    List<Item> getAllItemsByOwner(Integer ownerId);

    List<Item> searchItems(String text);
}
