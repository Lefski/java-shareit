package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
@Component
public class BookingMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),booking.getStart(),booking.getEnd(), booking.getItem(), booking.getBooker(), booking.getStatus());
    }

    public Booking toBooking(BookingDto booking) {
        /*
        Вот тут не знаю как корректно реализовать маппинг, потому что в DTO хранится только id итема, а не сам объект,
        поэтому получаю итем через репозиторий
        */
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("Item с переданным id не существует", HttpStatus.NOT_FOUND));
        User booker = userRepository.findById(booking.getBookerId()).orElseThrow(() -> new NotFoundException("Пользователя с переданным id не существует", HttpStatus.NOT_FOUND));
        return new Booking(booking.getStart(), booking.getEnd(), item, booker, booking.getStatus());
    }


}
