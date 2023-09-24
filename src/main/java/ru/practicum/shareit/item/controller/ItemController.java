package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @RequestParam(required = false) Integer requestId) {

        return itemService.addItem(itemDto, ownerId, requestId);

    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@PathVariable int itemId, @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("X-Sharer-User-Id header is missing.", HttpStatus.BAD_REQUEST);
        }
        return itemService.editItem(itemId, item, ownerId);

    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookings getItemById(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemById(itemId, userId);

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
        commentDto.setItemId(itemId);
        commentDto.setAuthorId(userId);
        return itemService.addCommentToItem(commentDto);

    }

}
