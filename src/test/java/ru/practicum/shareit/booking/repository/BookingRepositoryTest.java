package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User booker;
    private User owner;
    private Item item;
    private Booking pastBooking;
    private Booking futureBooking;


    @BeforeEach
    @Transactional
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM bookings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM items").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        owner = new User("Owner User", "owner@example.com");
        booker = new User("Booker User", "booker@example.com");
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);

        item = new Item("Test Item", "Item Description", true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        pastBooking = new Booking(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(12), item, booker, BookingStatus.APPROVED);
        futureBooking = new Booking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);

        bookingRepository.save(pastBooking);
        bookingRepository.save(futureBooking);
    }

    @Test
    void testFindNearestPastBooking() {
        Integer itemId = item.getId(); // Используем ID созданного item
        List<Booking> nearestPastBookings = bookingRepository.findNearestPastBooking(itemId);

        assertNotNull(nearestPastBookings);
        assertEquals(1, nearestPastBookings.size());
        assertEquals(pastBooking, nearestPastBookings.get(0));
    }

    @Test
    void testFindNearestFutureBooking() {
        Integer itemId = item.getId(); // Используем ID созданного item
        List<Booking> nearestFutureBookings = bookingRepository.findNearestFutureBooking(itemId);

        assertNotNull(nearestFutureBookings);
        assertEquals(1, nearestFutureBookings.size());
        assertEquals(futureBooking, nearestFutureBookings.get(0));
    }
}
