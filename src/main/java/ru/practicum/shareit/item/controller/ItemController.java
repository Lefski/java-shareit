package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.ErrorResponse;

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
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("X-Sharer-User-Id header is missing.", HttpStatus.BAD_REQUEST);
        }
        return itemService.addItem(itemDto, ownerId);

    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@PathVariable int itemId, @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("X-Sharer-User-Id header is missing.", HttpStatus.BAD_REQUEST);
        }
        return itemService.editItem(itemId, item, ownerId);

    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookings getItemById(@PathVariable int itemId) {
        return itemService.getItemById(itemId);

    }

    @GetMapping
    public List<ItemDtoWithBookings> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("X-Sharer-User-Id header is missing.", HttpStatus.BAD_REQUEST);
        }
        return itemService.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@PathVariable int itemId, @RequestBody CommentDto commentDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-Id header is missing.", HttpStatus.BAD_REQUEST);
        }
        commentDto.setItemId(itemId);
        commentDto.setAuthorId(userId);
        return itemService.addCommentToItem(commentDto);

    }

}
