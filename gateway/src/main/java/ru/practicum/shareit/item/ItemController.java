package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestBody @Valid ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Creating item {}, creatorId = {}", itemDto, ownerId);
        return itemClient.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(
            @PathVariable int itemId,
            @RequestBody @Valid ItemDto item,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("X-Sharer-User-Id header is missing.", HttpStatus.BAD_REQUEST);
        }
        log.info("Updating item {}, itemid = {}, creatorId = {}", item, itemId, ownerId);

        return itemClient.editItem(itemId, item, ownerId);

    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable int itemId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Getting item by itemId = {}, userId = {}", itemId, userId);

        return itemClient.getItemById(itemId, userId);

    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("X-Sharer-User-Id header is missing.", HttpStatus.BAD_REQUEST);
        }
        if (from < 0 || size <= 0) {
            throw new ValidationException("Передан некорректный параметр для пагинации", HttpStatus.BAD_REQUEST);
        }
        log.info("Getting all items, userId = {}, size = {}, from = {}", ownerId, size, from);

        return itemClient.getAllItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam String text) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Передан некорректный параметр для пагинации", HttpStatus.BAD_REQUEST);
        }
        log.info("Getting all items by search, size = {}, from = {}, text = {}", size, from, text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(
            @PathVariable int itemId,
            @Validated @RequestBody CommentDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        commentDto.setItemId(itemId);
        commentDto.setAuthorId(userId);
        log.info("Creating comment for itemId = {}, by userId = {}, comment {}", itemId, userId, commentDto);

        return itemClient.addCommentToItem(commentDto, itemId, userId);

    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(ValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getStatus());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }


}
