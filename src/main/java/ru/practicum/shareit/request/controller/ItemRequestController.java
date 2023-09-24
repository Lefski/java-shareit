package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.ErrorResponse;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") Integer userId) {
        if (itemRequestDto.getCreated() == null) {
            itemRequestDto.setCreated(LocalDateTime.now());
            //при получении ставим время получения запроса
        }

        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequests(
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        return itemRequestService.getAllItemRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public Page<ItemRequestDto> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "20") @Min(0) @Max(100) Integer size
    ) {
        Integer offset = from / size;
        //я не понял как можно получать page начиная с конкретного элемента, поэтому получаю элементы начиная со
        //страницы, на которой содержится первый элемент поиска

        return itemRequestService.getAllItemRequests(userId, offset, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam() Integer requestId
    ) {
        return itemRequestService.getRequestById(requestId);
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
