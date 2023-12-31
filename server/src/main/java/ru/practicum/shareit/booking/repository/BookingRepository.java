package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findBookingsByBooker_Id(int bookerId);

    List<Booking> findBookingsByItem_Owner_Id(int bookerId, Sort sort);

    List<Booking> findBookingsByItem_Owner_IdAndStatusEquals(int bookerId, BookingStatus status, Sort sort);

    List<Booking> findBookingsByItem_Owner_IdAndStartIsAfter(int bookerId, LocalDateTime start, Sort sort);

    List<Booking> findBookingsByItem_Owner_IdAndEndIsBefore(int bookerId, LocalDateTime end, Sort sort);

    List<Booking> findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(int bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findBookingsByBooker_Id(int bookerId, Sort sort);

    List<Booking> findBookingsByBooker_IdAndStatusEquals(int bookerId, BookingStatus status, Sort sort);

    List<Booking> findBookingsByBooker_IdAndStartIsAfter(int bookerId, LocalDateTime start, Sort sort);

    List<Booking> findBookingsByBooker_IdAndEndIsBefore(int bookerId, LocalDateTime end, Sort sort);

    List<Booking> findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(int bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.start < current_timestamp AND b.item.id = :itemId AND b.status = 'APPROVED' ORDER BY b.end DESC")
    List<Booking> findNearestPastBooking(@Param("itemId") Integer itemId);

    @Query("SELECT b FROM Booking b WHERE b.start > current_timestamp AND b.item.id = :itemId AND b.status = 'APPROVED' ORDER BY b.end ASC")
    List<Booking> findNearestFutureBooking(@Param("itemId") Integer itemId);
}
