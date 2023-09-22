package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
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
    /* Я не до конца понял тз, потому что на вход в тестах дается поле itemId, возвращаться в числе прочего должен
    полноценный item. Получается, нужно два разных поля. Но если я возвращаю целый объект, на одном из этапов
    сериализации происходит ошибка из-за FetchType.LAZY, я так и не понял где именно. Поэтому везде использую
    FetchType.EAGER. Я понимаю, что это не совсем корректно, но не смог разобраться в ошибке*/

    public BookingDto() {
    }

    public BookingDto(Integer itemId, LocalDateTime start, LocalDateTime end) {
        this.itemId = itemId;
        this.start = start;
        this.end = end;
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
