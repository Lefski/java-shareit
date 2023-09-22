package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Override
    public ItemDto addItem(ItemDto itemDto, Integer ownerId) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Отсутствует статус Available Item", HttpStatus.BAD_REQUEST);
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Некорректное имя Item", HttpStatus.BAD_REQUEST);
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Некорректное описание Item", HttpStatus.BAD_REQUEST);
        }

        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Владельца с переданным id не существует", HttpStatus.NOT_FOUND));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    public ItemDto editItem(Integer itemId, ItemDto itemDto, Integer ownerId) {
        Item oldItem = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item с переданным id не существует", HttpStatus.NOT_FOUND));
        oldItem.setId(itemId);
        if (oldItem.getOwner().getId() != ownerId) {
            throw new NotFoundException("У вещи другой владелец", HttpStatus.NOT_FOUND);
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(repository.save(oldItem));
    }

    @Override
    public ItemDtoWithBookings getItemById(Integer itemId, Integer userId) {
        ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item с переданным id не существует", HttpStatus.NOT_FOUND)));
        Item checkItem = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item с переданным id не существует", HttpStatus.NOT_FOUND));
        if (checkItem.getOwner().getId() == userId) {
            itemDtoWithBookings = checkItemBookings(itemDtoWithBookings, itemId);
        }
        itemDtoWithBookings.setComments(commentRepository.findAllByItem_Id(itemId));
        return itemDtoWithBookings;
    }

    @Override
    public List<ItemDtoWithBookings> getAllItemsByOwner(Integer ownerId) {
        List<Item> itemList = repository.findByOwnerId(ownerId);
        ArrayList<ItemDtoWithBookings> itemDtos = new ArrayList<>();
        for (Item item : itemList) {
            ItemDtoWithBookings itemDtoWithBooking = ItemMapper.toItemDtoWithBookings(item);
            itemDtoWithBooking = checkItemBookings(itemDtoWithBooking, ownerId);
            itemDtoWithBooking.setComments(commentRepository.findAllByItem_Id(itemDtoWithBooking.getId()));
            itemDtos.add(itemDtoWithBooking);
        }
        Comparator<ItemDtoWithBookings> idComparator = Comparator.comparingInt(ItemDtoWithBookings::getId);

        // Отсортируем список, из-за каких-то операций в бд элементы расположены не по порядку, возможно неправильно
        // произвожу транзакции, не понял как откорректировать результат в бд, поэтому сортирую на месте
        Collections.sort(itemDtos, idComparator);
        return itemDtos;
    }

    private ItemDtoWithBookings checkItemBookings(ItemDtoWithBookings itemDtoWithBooking, int itemId) {

        List<Booking> lastBookingList = bookingRepository.findNearestPastBooking(LocalDateTime.now(), itemId);
        Booking lastBooking = null;
        if (lastBookingList.size() > 0) {
            lastBooking = lastBookingList.get(0);
            lastBooking.setBookerId(lastBooking.getBooker().getId());
        }
        itemDtoWithBooking.setLastBooking(lastBooking);
        List<Booking> nextBookingList = bookingRepository.findNearestFutureBooking(LocalDateTime.now(), itemId);
        Booking nextBooking = null;
        if (nextBookingList.size() > 0) {
            nextBooking = nextBookingList.get(0);
            nextBooking.setBookerId(nextBooking.getBooker().getId());
        }
        itemDtoWithBooking.setNextBooking(nextBooking);
        return itemDtoWithBooking;
    }

    @Override
    public CommentDto addCommentToItem(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank() || commentDto.getText().isEmpty()) {
            throw new ValidationException("Пустой комментарий", HttpStatus.BAD_REQUEST);
        }
        int authorId = commentDto.getAuthorId();
        int itemId = commentDto.getItemId();
        List<Booking> bookingList = bookingRepository.findBookingsByBooker_Id(authorId);
        ArrayList<Booking> bookingsConfirmed = new ArrayList<>();
        for (Booking booking : bookingList) {
            if (booking.getItem().getId() == itemId && booking.getEnd().isBefore(LocalDateTime.now())) {
                bookingsConfirmed.add(booking);
            }
        }
        if (bookingsConfirmed.size() < 1) {
            throw new ValidationException("Передано некорректное бронирование", HttpStatus.BAD_REQUEST);
        }

        Comment comment = commentRepository.save(commentMapper.toComment(commentDto));
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemList = repository.search(text);
        ArrayList<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemList) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }
}
