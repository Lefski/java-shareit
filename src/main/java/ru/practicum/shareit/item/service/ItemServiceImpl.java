package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Storage.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage inMemoryItemStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    private final ItemRepository repository;
    private final UserRepository userRepository;


    @Override
    public ItemDto addItem(ItemDto itemDto, Integer ownerId) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Отсутствует статус Available Item", HttpStatus.BAD_REQUEST);
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Некорректное имя Item", HttpStatus.BAD_REQUEST);
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Некорректное описание Item", HttpStatus.BAD_REQUEST);
        }

        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Владельца с переданным id не существует", HttpStatus.NOT_FOUND));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    public ItemDto editItem(Integer itemId, ItemDto itemDto, Integer ownerId) {
        Item oldItem = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item с переданным id не существует", HttpStatus.NOT_FOUND));
        oldItem.setId(itemId);
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
        return ItemMapper.toItemDto(repository.save(oldItem));
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return ItemMapper.toItemDto(repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item с переданным id не существует", HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Integer ownerId) {
        List<Item> itemList = repository.findByOwnerId(ownerId);
        ArrayList<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemList) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if(text.isBlank() || text.isEmpty()){
            return new ArrayList<>();
        }
        List<Item> itemList = repository.search(text);
        ArrayList<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemList) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }
}
