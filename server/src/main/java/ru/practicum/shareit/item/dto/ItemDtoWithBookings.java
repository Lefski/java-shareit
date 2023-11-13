package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Data
public class ItemDtoWithBookings {
    Booking lastBooking;
    Booking nextBooking;
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
    private List<CommentDto> comments;


    public ItemDtoWithBookings(int id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemDtoWithBookings(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ItemDtoWithBookings() {
    }
}
