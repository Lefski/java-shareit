package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Storage.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.User;

import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage inMemoryItemStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage inMemoryItemStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryItemStorage = inMemoryItemStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public Item addItem(ItemDto itemDto, Integer ownerId) {
        User owner = inMemoryUserStorage.getUserById(ownerId);
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Отсутствует статус Available ItemDto", HttpStatus.BAD_REQUEST);
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Некорректное имя ItemDto", HttpStatus.BAD_REQUEST);
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Некорректное описание ItemDto", HttpStatus.BAD_REQUEST);
        }
        return inMemoryItemStorage.add(ItemMapper.toItem(itemDto), owner);
    }

    @Override
    public Item editItem(Integer itemId, ItemDto itemDto, Integer ownerId) {
        return inMemoryItemStorage.update(itemId, itemDto, ownerId);
    }

    @Override
    public Item getItemById(Integer itemId) {
        return inMemoryItemStorage.getItemById(itemId);
    }

    @Override
    public List<Item> getAllItemsByOwner(Integer ownerId) {
        return inMemoryItemStorage.getAllItemsByOwner(ownerId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return inMemoryItemStorage.searchItems(text);
    }
}
