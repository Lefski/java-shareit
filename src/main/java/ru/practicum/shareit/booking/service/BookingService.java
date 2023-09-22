package ru.practicum.shareit.booking.service;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
@Slf4j
@Transactional
public class BookingService {
    private final static Sort DESCENDED_SORT = Sort.by(Sort.Direction.DESC, "start");

    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemServiceImpl itemService;
    private final BookingMapper bookingMapper;


    public BookingDto addBooking(BookingDto bookingDto) {
        log.info("Выполняется запрос на создание бронирования {}", bookingDto.toString());
        int bookerId = bookingDto.getBookerId();
        bookingValidation(bookingDto, bookerId);
        Booking booking = bookingMapper.toBooking(bookingDto);
        Booking savedBooking = repository.save(booking);
        log.debug("Выполнено создание бронирования {}", savedBooking);
        return bookingMapper.toBookingDto(savedBooking);
    }

    public boolean isBookingOwner(int bookingId, int bookerId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> new ValidationException("Бронирования с переданным id не существует", HttpStatus.NOT_FOUND));

        return booking.getItem().getOwner().getId() == bookerId;
    }

    public BookingDto approveBooking(int bookingId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> new ValidationException("Бронирования с переданным id не существует", HttpStatus.NOT_FOUND));
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Заявка уже обработана, ее текущий статус: " + booking.getStatus(), HttpStatus.BAD_REQUEST);
        }
        booking.setStatus(BookingStatus.APPROVED);
        return bookingMapper.toBookingDto(repository.save(booking));
    }

    public BookingDto rejectBooking(int bookingId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> new ValidationException("Бронирования с переданным id не существует", HttpStatus.BAD_REQUEST));
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Заявка уже обработана, ее текущий статус: " + booking.getStatus(), HttpStatus.BAD_REQUEST);
        }
        booking.setStatus(BookingStatus.REJECTED);
        return bookingMapper.toBookingDto(repository.save(booking));
    }

    public void bookingValidation(BookingDto bookingDto, Integer bookerId) {
        UserDto booker = userService.getUserById(bookerId);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Item с переданным id не существует", HttpStatus.NOT_FOUND));
        /*
        Я решил что нет смысла накручивать дополнительные проверки существования юзера и итема, всё равно я уже написал
        проверки в соответствующих сервисах, ими и воспользуюсь. Я понимаю, что это создает лишнюю нагрузку,
        но в проекте слишком маленький объем запросов, чтобы это на что-то повлияло.
        */
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования", HttpStatus.BAD_REQUEST);
        }
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start == null || end == null || end.isBefore(start) || start.isEqual(end) || start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Неправильно указано время начала или конца бронирования", HttpStatus.BAD_REQUEST);
        }
        if (booker.getId() == item.getOwner().getId()) {
            //нельзя арендовать у самого себя
            throw new ValidationException("Нельзя арендовать у самого себя", HttpStatus.NOT_FOUND);

        }

    }


    public boolean isBooker(Integer bookingId, Integer userId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> new ValidationException("Бронирования с переданным id не существует", HttpStatus.BAD_REQUEST));
        return booking.getBooker().getId() == userId;
    }

    public BookingDto getBookingById(Integer bookingId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> new ValidationException("Бронирования с переданным id не существует", HttpStatus.BAD_REQUEST));
        return bookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getUserBookings(String state, int userId) {
        repository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с переданным id не существует", HttpStatus.NOT_FOUND));

        List<Booking> userBookings;
        switch (state.toUpperCase()) {
            case "CURRENT":
                userBookings = getCurrentUserBookings(userId);
                break;
            case "PAST":
                userBookings = getPastUserBookings(userId);
                break;
            case "FUTURE":
                userBookings = getFutureUserBookings(userId);
                break;
            case "WAITING":
                userBookings = getWaitingUserBookings(userId);
                break;
            case "REJECTED":
                userBookings = getRejectedUserBookings(userId);
                break;
            default:
                userBookings = getAllUserBookings(userId);
                break;
        }
        ArrayList<BookingDto> userBookingsDto = new ArrayList<>();
        for (Booking booking :
                userBookings) {
            userBookingsDto.add(bookingMapper.toBookingDto(booking));
        }
        return userBookingsDto;
    }

    public List<BookingDto> getOwnerBookings(String state, int userId) {
        repository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с переданным id не существует", HttpStatus.NOT_FOUND));

        List<Booking> userBookings;
        switch (state.toUpperCase()) {
            case "CURRENT":
                userBookings = getCurrentOwnerBookings(userId);
                break;
            case "PAST":
                userBookings = getPastOwnerBookings(userId);
                break;
            case "FUTURE":
                userBookings = getFutureOwnerBookings(userId);
                break;
            case "WAITING":
                userBookings = getWaitingOwnerBookings(userId);
                break;
            case "REJECTED":
                userBookings = getRejectedOwnerBookings(userId);
                break;
            default:
                userBookings = getAllOwnerBookings(userId);
                break;
        }
        ArrayList<BookingDto> userBookingsDto = new ArrayList<>();
        for (Booking booking :
                userBookings) {
            userBookingsDto.add(bookingMapper.toBookingDto(booking));
        }
        return userBookingsDto;
    }

    private List<Booking> getCurrentOwnerBookings(int userId) {
        return repository.findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), DESCENDED_SORT);
    }

    private List<Booking> getPastOwnerBookings(int userId) {
        return repository.findBookingsByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), DESCENDED_SORT);
    }

    private List<Booking> getFutureOwnerBookings(int userId) {
        return repository.findBookingsByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), DESCENDED_SORT);
    }

    private List<Booking> getWaitingOwnerBookings(int userId) {
        return repository.findBookingsByItem_Owner_IdAndStatusEquals(userId, BookingStatus.WAITING, DESCENDED_SORT);
    }

    private List<Booking> getRejectedOwnerBookings(int userId) {
        return repository.findBookingsByItem_Owner_IdAndStatusEquals(userId, BookingStatus.REJECTED, DESCENDED_SORT);
    }

    private List<Booking> getAllOwnerBookings(int userId) {
        return repository.findBookingsByItem_Owner_Id(userId, DESCENDED_SORT);
    }

    private List<Booking> getCurrentUserBookings(int userId) {
        return repository.findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), DESCENDED_SORT);
    }

    private List<Booking> getPastUserBookings(int userId) {
        return repository.findBookingsByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), DESCENDED_SORT);
    }

    private List<Booking> getFutureUserBookings(int userId) {
        return repository.findBookingsByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), DESCENDED_SORT);
    }

    private List<Booking> getWaitingUserBookings(int userId) {
        return repository.findBookingsByBooker_IdAndStatusEquals(userId, BookingStatus.WAITING, DESCENDED_SORT);
    }

    private List<Booking> getRejectedUserBookings(int userId) {
        return repository.findBookingsByBooker_IdAndStatusEquals(userId, BookingStatus.REJECTED, DESCENDED_SORT);
    }

    private List<Booking> getAllUserBookings(int userId) {
        return repository.findBookingsByBooker_Id(userId, DESCENDED_SORT);
    }


}
