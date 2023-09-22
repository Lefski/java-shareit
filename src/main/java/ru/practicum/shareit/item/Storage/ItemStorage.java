package ru.practicum.shareit.item.Storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    Item add(Item item, User owner);

    Item update(Integer itemId, ItemDto itemDto, Integer ownerId);

    Item getItemById(Integer itemId);

    List<Item> getAllItemsByOwner(Integer ownerId);

    List<Item> searchItems(String text);
}
