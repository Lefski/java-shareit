package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookingServiceTest {
    private static final Sort DESCENDED_SORT = Sort.by(Sort.Direction.DESC, "start");

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingMapper bookingMapper;

    private BookingDto bookingDto;
    private Booking booking;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1);
        commentDto.setItemId(1);
        commentDto.setText("Test Comment");

        booking = new Booking();
        booking.setId(1);
        booking.setEnd(LocalDateTime.now().plusMinutes(1));
        booking.setStart(LocalDateTime.now().plusSeconds(10));
        user = new User("User", "user@example.com");
        user.setId(1);
        booking.setBooker(user);
        item = new Item("Item 1", "Item 1 Description", true);
        item.setId(1);
        booking.setItem(item);
        bookingDto = new BookingDto();
        bookingDto.setId(1);
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(1));
        bookingDto.setStart(LocalDateTime.now().plusSeconds(10));
        bookingDto.setBookerId(1);
        bookingDto.setItemId(1);
        bookingDto.setItem(item);
    }

    @Test
    void testAddBooking() {
        Integer bookerId = 1;

        bookingDto.setBookerId(bookerId);
        UserDto booker = UserMapper.toUserDto(user);
        user.setId(2);
        item.setOwner(user);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(bookingDto);
        when(bookingMapper.toBooking(any(BookingDto.class))).thenReturn(booking);

        BookingDto result = bookingService.addBooking(bookingDto, bookerId);
        assertNotNull(result);
        assertEquals(bookingDto, result);
    }

    @Test
    void testApproveOrRejectBookingWithValidBookingOwner() {
        Integer bookingId = 1;
        Integer bookerId = 1;
        UserDto booker = UserMapper.toUserDto(user);
        user.setId(1);
        item.setOwner(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(bookingDto);
        when(bookingMapper.toBooking(any(BookingDto.class))).thenReturn(booking);

        BookingDto result = bookingService.approveOrRejectBooking(bookingId, bookerId, true);
        assertNotNull(result);
    }

    @Test
    void testRejectBookingWithValidBookingOwner() {
        Integer bookingId = 1;
        Integer bookerId = 1;
        UserDto booker = UserMapper.toUserDto(user);
        user.setId(1);
        item.setOwner(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(bookingDto);
        when(bookingMapper.toBooking(any(BookingDto.class))).thenReturn(booking);

        BookingDto result = bookingService.approveOrRejectBooking(bookingId, bookerId, false);
        assertNotNull(result);
    }

    @Test
    void testApproveOrRejectBookingWithInvalidBookingOwner() {
        Integer bookingId = 1;
        Integer bookerId = 1;
        UserDto booker = UserMapper.toUserDto(user);
        user.setId(3);
        item.setOwner(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(bookingDto);
        when(bookingMapper.toBooking(any(BookingDto.class))).thenReturn(booking);

        assertThrows(ValidationException.class, () -> bookingService.approveOrRejectBooking(bookingId, bookerId, true));
    }

    @Test
    void testRejectBookingWithInvalidBookingOwner() {
        Integer bookingId = 1;
        Integer bookerId = 1;
        UserDto booker = UserMapper.toUserDto(user);
        user.setId(3);
        item.setOwner(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(bookingDto);
        when(bookingMapper.toBooking(any(BookingDto.class))).thenReturn(booking);

        assertThrows(ValidationException.class, () -> bookingService.approveOrRejectBooking(bookingId, bookerId, false));
    }

    @Test
    void testIsBookingOwnerWithValidOwner() {
        Integer bookingId = 1;
        Integer bookerId = 1;

        Item item = new Item();
        User owner = new User();
        owner.setId(bookerId);
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        boolean result = bookingService.isBookingOwner(bookingId, bookerId);
        assertTrue(result);
    }

    @Test
    void testIsBookingOwnerWithInvalidOwner() {
        Integer bookingId = 1;
        Integer bookerId = 1;

        Item item = new Item();
        User owner = new User();
        owner.setId(2); // Not the same as bookerId
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        boolean result = bookingService.isBookingOwner(bookingId, bookerId);
        assertFalse(result);
    }


    @Test
    void testApproveBookingWithInvalidStatus() {
        Integer bookingId = 1;

        booking.setStatus(BookingStatus.APPROVED); // Already approved

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(bookingId));
    }


    @Test
    void testRejectBookingWithInvalidStatus() {
        Integer bookingId = 1;

        booking.setStatus(BookingStatus.APPROVED); // Already approved

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.rejectBooking(bookingId));
    }

    @Test
    void testBookingValidationWithValidInput() {
        Integer bookerId = 1;
        bookingDto.setBookerId(bookerId);
        UserDto booker = UserMapper.toUserDto(user);
        item.setAvailable(true);
        user.setId(2);
        item.setOwner(user);
        booking.setItem(item);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.of(item));
        bookingService.bookingValidation(bookingDto, bookerId);
        assertDoesNotThrow(() -> bookingService.bookingValidation(bookingDto, bookerId));
    }

    @Test
    void testBookingValidationWithUnavailableItem() {
        Integer bookerId = 1;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setBookerId(bookerId);

        UserDto booker = new UserDto();
        Item item = new Item();
        item.setAvailable(false);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.bookingValidation(bookingDto, bookerId));
    }

    @Test
    void testBookingValidationWithInvalidTime() {
        Integer bookerId = 1;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setBookerId(bookerId);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().minusHours(1));

        UserDto booker = new UserDto();
        Item item = new Item();
        item.setAvailable(true);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.bookingValidation(bookingDto, bookerId));
    }

    @Test
    void testBookingValidationWithSameOwner() {
        Integer bookerId = 1;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setBookerId(bookerId);

        UserDto booker = new UserDto();
        Item item = new Item();
        item.setAvailable(true);
        User owner = new User();
        owner.setId(bookerId);

        when(userService.getUserById(bookerId)).thenReturn(booker);
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.of(item));
        item.setOwner(owner);

        assertThrows(NotFoundException.class, () -> bookingService.bookingValidation(bookingDto, bookerId));
    }

    @Test
    void testIsBookerWithValidBooker() {
        Integer bookingId = 1;
        Integer userId = 1;

        User booker = new User();
        booker.setId(userId);
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        boolean result = bookingService.isBooker(bookingId, userId);
        assertTrue(result);
    }

    @Test
    void testIsBookerWithInvalidBooker() {
        Integer bookingId = 1;
        Integer userId = 1;

        User booker = new User();
        booker.setId(2); // Not the same as userId
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        boolean result = bookingService.isBooker(bookingId, userId);
        assertFalse(result);
    }

    @Test
    void testGetBookingByIdWithValidBooking() {
        Integer bookingId = 1;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(bookingId);
        assertNotNull(result);
        assertEquals(bookingDto, result);
    }

    @Test
    void testGetBookingByIdWithInvalidBooking() {
        Integer bookingId = 1;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> bookingService.getBookingById(bookingId));
    }

    @Test
    void testGetUserBookings() {
        Integer userId = 1;
        String state = "CURRENT";
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(userId)).thenReturn(Optional.of(booking));
        when(bookingRepository.findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), DESCENDED_SORT))
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getUserBookings(state, userId, from, size);
        assertNotNull(result);
        assertEquals(bookings, result);
    }

    @Test
    void testGetOwnerBookings() {
        Integer userId = 1;
        String state = "CURRENT";
        int from = 0;
        int size = 10;

        List<Booking> bookings = new ArrayList<>();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), DESCENDED_SORT))
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getOwnerBookings(state, userId, from, size);
        assertNotNull(result);
    }
}
