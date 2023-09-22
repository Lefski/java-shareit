package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.ErrorResponse;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    List<String> states;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Integer bookerId) {
        if (bookerId == null) {
            throw new ValidationException("X-Sharer-User-id header is missing", HttpStatus.BAD_REQUEST);
        }
        
        bookingDto.setBookerId(bookerId);
        return bookingService.addBooking(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(
            @PathVariable Integer bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Integer bookerId
    ) {
        if (bookerId == null) {
            throw new ValidationException("X-Sharer-User-id header is missing", HttpStatus.BAD_REQUEST);
        }

        if (bookingService.isBookingOwner(bookingId, bookerId)) {
            if (approved) {
                // Если approved=true, подтверждаем бронирование
                return bookingService.approveBooking(bookingId);
            } else {
                // Если approved=false, отклоняем бронирование
                return bookingService.rejectBooking(bookingId);
            }
        } else {
            throw new ValidationException("У вас нет доступа к данному бронированию", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingDetails(
            @PathVariable Integer bookingId,
            @RequestHeader("X-Sharer-User-Id") Integer bookerId
    ) {
        if (bookerId == null) {
            throw new ValidationException("X-Sharer-User-id header is missing", HttpStatus.BAD_REQUEST);
        }
        if (bookingService.isBookingOwner(bookingId, bookerId) || bookingService.isBooker(bookingId, bookerId)) {
            return bookingService.getBookingById(bookingId);
        } else {
            throw new ValidationException("У вас нет доступа к данному бронированию", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-id header is missing", HttpStatus.BAD_REQUEST);
        }
        states = List.of("CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED", "ALL");
        if (!states.contains(state)) {
            throw new ValidationException("Unknown state: " + state, HttpStatus.BAD_REQUEST);
        }
        return bookingService.getOwnerBookings(state, userId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        if (userId == null) {
            throw new ValidationException("X-Sharer-User-id header is missing", HttpStatus.BAD_REQUEST);
        }
        states = List.of("CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED", "ALL");
        if (!states.contains(state)) {
            throw new ValidationException("Unknown state: " + state, HttpStatus.BAD_REQUEST);
        }
        return bookingService.getUserBookings(state, userId);
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
