package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.ErrorResponse;

import java.util.List;

@RestController
@RequestMapping("/items")

public class ItemController {

    ItemService itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemService.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@PathVariable int itemId, @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemService.editItem(itemId, item, ownerId);

    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookings getItemById(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemById(itemId, userId);

    }

    @GetMapping
    public List<ItemDtoWithBookings> getAllItemsByOwner(
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemService.getAllItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam String text) {
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@PathVariable int itemId, @RequestBody CommentDto commentDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        commentDto.setItemId(itemId);
        commentDto.setAuthorId(userId);
        return itemService.addCommentToItem(commentDto);

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
