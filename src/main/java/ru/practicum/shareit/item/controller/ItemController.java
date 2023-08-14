package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.ErrorResponse;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {


    private final ItemServiceImpl itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item addItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        //уважаемый проверяющий, добрый день, по какой-то причине не сериализуются объекты ItemDto, не понимаю почему
        //выдает ошибку, поэтому преобразую Item в ItemDto с помощью ItemMapper
        if (ownerId == null) {
            throw new ValidationException("X-Sharer-User-Id header is missing.", HttpStatus.BAD_REQUEST);
        }
        return itemService.addItem(ItemMapper.toItemDto(item), ownerId);
    }

    @PatchMapping("/{itemId}")
    public Item editItem(@PathVariable int itemId, @RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("X-Sharer-User-Id header is missing.", HttpStatus.BAD_REQUEST);
        }
        return itemService.editItem(itemId, ItemMapper.toItemDto(item), ownerId);

    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable int itemId) {
        return itemService.getItemById(itemId);

    }

    @GetMapping
    public List<Item> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("X-Sharer-User-Id header is missing.", HttpStatus.BAD_REQUEST);
        }
        return itemService.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(ValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getStatus());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getStatus());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }
}
