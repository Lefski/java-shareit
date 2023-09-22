package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

@Data
public class ItemDtoWithBookings extends ItemDto {
     Booking lastBooking;
    Booking nextBooking;

    public ItemDtoWithBookings(int id, String name, String description, Boolean available) {
        super(id, name, description, available);
    }

}
