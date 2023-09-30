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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    private static List<ItemDtoWithBookings> paging(int from, int size, List<ItemDtoWithBookings> itemDtoWithBookingsList) {
        List<ItemDtoWithBookings> bookingDtosPage = new ArrayList<>();
        if (size != 0 && from < itemDtoWithBookingsList.size()) {
            int i = from;
            int sizeCounter = 0;
            while (i < itemDtoWithBookingsList.size() && sizeCounter < size) {
                bookingDtosPage.add(itemDtoWithBookingsList.get(i));
                i++;
                sizeCounter++;
            }
        }
        return bookingDtosPage;
    }

    public static List<ItemDto> pagingForSearch(int from, int size, List<ItemDto> itemDtoList) {
        List<ItemDto> bookingDtosPage = new ArrayList<>();
        if (size != 0 && from < itemDtoList.size()) {
            int i = from;
            int sizeCounter = 0;
            while (i < itemDtoList.size() && sizeCounter < size) {
                bookingDtosPage.add(itemDtoList.get(i));
                i++;
                sizeCounter++;
            }
        }
        return bookingDtosPage;
    }

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
        Integer requestId = null;
        if (itemDto.getRequestId() != null) {
            requestId = itemDto.getRequestId();
        }
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Владельца с переданным id не существует", HttpStatus.NOT_FOUND));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        if (requestId != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запроса с переданным id не существует", HttpStatus.NOT_FOUND));
            item.setRequest(itemRequest);
            ItemDto itemDtoWithRequest = ItemMapper.toItemDto(repository.save(item));
            itemDtoWithRequest.setRequestId(itemRequest.getId());
            return itemDtoWithRequest;
        } else {
            return ItemMapper.toItemDto(repository.save(item));
        }

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
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = commentMapper.toCommentDto(comment);
            commentDto.setAuthorName(comment.getAuthor().getName());
            commentsDto.add(commentDto);
        }
        itemDtoWithBookings.setComments(commentsDto);
        return itemDtoWithBookings;
    }

    @Override
    public List<ItemDtoWithBookings> getAllItemsByOwner(Integer ownerId, Integer from, Integer size) {
        List<Item> itemList = repository.findByOwnerId(ownerId);
        List<ItemDtoWithBookings> itemDtos = new ArrayList<>();
        for (Item item : itemList) {
            ItemDtoWithBookings itemDtoWithBooking = ItemMapper.toItemDtoWithBookings(item);
            itemDtoWithBooking = checkItemBookings(itemDtoWithBooking, item.getId());
            List<Comment> comments = commentRepository.findAllByItem_Id(itemDtoWithBooking.getId());
            List<CommentDto> commentsDto = new ArrayList<>();
            for (Comment comment : comments) {
                CommentDto commentDto = commentMapper.toCommentDto(comment);
                commentDto.setAuthorName(comment.getAuthor().getName());
                commentsDto.add(commentDto);
            }
            itemDtoWithBooking.setComments(commentsDto);
            itemDtos.add(itemDtoWithBooking);
        }
        Comparator<ItemDtoWithBookings> idComparator = Comparator.comparingInt(ItemDtoWithBookings::getId);

        // Отсортируем список, из-за каких-то операций в бд элементы расположены не по порядку, возможно неправильно
        // произвожу транзакции, не понял как откорректировать результат в бд, поэтому сортирую на месте
        itemDtos.sort(idComparator);
        itemDtos = paging(from, size, itemDtos);
        return itemDtos;
    }

    private ItemDtoWithBookings checkItemBookings(ItemDtoWithBookings itemDtoWithBooking, int itemId) {

        List<Booking> lastBookingList = bookingRepository.findNearestPastBooking(itemId);
        Booking lastBooking = null;
        if (lastBookingList.size() > 0) {
            lastBooking = lastBookingList.get(0);
            lastBooking.setBookerId(lastBooking.getBooker().getId());
        }
        itemDtoWithBooking.setLastBooking(lastBooking);
        List<Booking> nextBookingList = bookingRepository.findNearestFutureBooking(itemId);
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

    @Override
    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemList = repository.search(text);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemList) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        itemDtos = pagingForSearch(from, size, itemDtos);
        return itemDtos;
    }
}
