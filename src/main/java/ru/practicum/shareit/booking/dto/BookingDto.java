package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@ToString(includeFieldNames = false)
public class BookingDto {
    private static final BookingStatus DEFAULT_SATUS = BookingStatus.WAITING;

    private int id;
    private Integer itemId;
    private Integer bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status = DEFAULT_SATUS;

    public BookingDto() {
    }

    public BookingDto(int id, LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }

    public BookingDto(int id, Item item, User booker, BookingStatus status) {
        this.id = id;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }


}
