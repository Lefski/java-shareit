package ru.practicum.shareit.item.Storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ItemStorageImpl implements ItemStorage {
    Map<Integer, Item> items = new HashMap<>();
    private int idCounter = 1;


    @Override
    public Item add(Item item, User owner) {
        item.setOwner(owner);
        item.setId(idCounter);
        idCounter++;
        log.debug("Добавлен item: {}", item);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Integer itemId, ItemDto itemDto, Integer ownerId) {
        Item oldItem = getItemById(itemId);
        if (oldItem.getOwner().getId() != ownerId) {
            throw new NotFoundException("У вещи другой владелец", HttpStatus.NOT_FOUND);
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        items.put(itemId, oldItem);
        return oldItem;
    }

    @Override
    public Item getItemById(Integer id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Item с переданным id не существует");
        }
        return items.get(id);
    }

    @Override
    public List<Item> getAllItemsByOwner(Integer ownerId) {
        List<Item> filteredItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() != null && item.getOwner().getId() == ownerId) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> filteredItems = new ArrayList<>();
        if (text.isBlank() || text.isEmpty()) {
            return filteredItems;
        }
        for (Item item : items.values()) {
            if (item.getDescription() != null && item.getName() != null) {
                if (item.getDescription().toLowerCase().contains(text.toLowerCase()) || item.getName().toLowerCase().contains(text.toLowerCase())) {
                    if (item.isAvailable()) {
                        filteredItems.add(item);
                    }
                }
            }
        }
        return filteredItems;
    }
}
