package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BookingMapperTest {

    @InjectMocks
    private BookingMapper bookingMapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToBookingDto() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setStatus(BookingStatus.APPROVED);

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertNotNull(bookingDto);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void testToBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1);
        bookingDto.setBookerId(2);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingDto.setStatus(BookingStatus.WAITING);

        Item item = new Item();
        item.setId(1);
        User booker = new User();
        booker.setId(2);

        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(bookingDto.getBookerId())).thenReturn(Optional.of(booker));

        Booking booking = bookingMapper.toBooking(bookingDto);

        assertNotNull(booking);
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(bookingDto.getStatus(), booking.getStatus());
    }

    @Test
    void testToBookingWithMissingItem() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1);
        bookingDto.setBookerId(2);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingDto.setStatus(BookingStatus.WAITING);

        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingMapper.toBooking(bookingDto));
    }

    @Test
    void testToBookingWithMissingUser() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1);
        bookingDto.setBookerId(2);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingDto.setStatus(BookingStatus.WAITING);

        Item item = new Item();
        item.setId(1);

        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(bookingDto.getBookerId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingMapper.toBooking(bookingDto));
    }

}
