package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddItem() {
        ItemDto itemDto = new ItemDto("Test Item", "Item Description", true);
        Integer ownerId = 1;
        User owner = new User("Owner", "owner@example.com");
        owner.setId(ownerId);
        Item savedItem = new Item("Test Item", "Item Description", true);
        savedItem.setOwner(owner);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto resultItemDto = itemService.addItem(itemDto, ownerId);

        assertNotNull(resultItemDto);
        assertEquals(itemDto.getName(), resultItemDto.getName());
        assertEquals(itemDto.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    void testAddItemWithMissingAvailable() {
        ItemDto itemDto = new ItemDto("Test Item", "Item Description", null);
        Integer ownerId = 1;

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDto, ownerId));
    }

    @Test
    void testAddItemWithMissingName() {
        ItemDto itemDto = new ItemDto(null, "Item Description", true);
        Integer ownerId = 1;

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDto, ownerId));
    }

    @Test
    void testAddItemWithMissingDescription() {
        ItemDto itemDto = new ItemDto("Test Item", null, true);
        Integer ownerId = 1;

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDto, ownerId));
    }

    @Test
    void testEditItem() {
        Integer itemId = 1;
        Integer ownerId = 1;
        User owner = new User("Owner", "owner@example.com");
        owner.setId(ownerId);
        ItemDto itemDto = new ItemDto("Updated Item", "Updated Description", true);
        Item existingItem = new Item("Test Item", "Item Description", true);
        existingItem.setId(itemId);
        existingItem.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        ItemDto resultItemDto = itemService.editItem(itemId, itemDto, ownerId);

        assertNotNull(resultItemDto);
        assertEquals(itemDto.getName(), resultItemDto.getName());
        assertEquals(itemDto.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), resultItemDto.getAvailable());
    }


    @Test
    void testGetItemById() {
        Integer itemId = 1;
        Integer userId = 1;
        User owner = new User("Owner", "owner@example.com");
        owner.setId(userId);
        Item item = new Item("Test Item", "Item Description", true);
        item.setId(itemId);
        item.setOwner(owner);

        // Устанавливаем поведение для заглушек
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // Вызываем метод и проверяем результат
        ItemDtoWithBookings resultItemDto = itemService.getItemById(itemId, userId);

        assertNotNull(resultItemDto);
        assertEquals(item.getName(), resultItemDto.getName());
        assertEquals(item.getDescription(), resultItemDto.getDescription());
        assertEquals(item.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    void testGetItemByIdWithInvalidItem() {
        Integer itemId = 1;
        Integer userId = 1;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId, userId));
    }


    @Test
    void testGetAllItemsByOwner() {
        Integer ownerId = 1;
        Integer from = 0;
        Integer size = 10;
        User owner = new User("Owner", "owner@example.com");
        owner.setId(ownerId);
        User booker = new User("Booker", "booker@example.com");
        booker.setId(3);
        Booking nextBooking = new Booking();
        nextBooking.setBooker(booker);
        Booking pastBooking = new Booking();
        pastBooking.setBooker(booker);
        Item item1 = new Item("Item 1", "Item Description 1", true);
        item1.setId(1);
        item1.setOwner(owner);
        Item item2 = new Item("Item 2", "Item Description 2", true);
        item2.setId(2);
        item2.setOwner(owner);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item2);
        itemList.add(item1);

        // Устанавливаем поведение для заглушек
        when(itemRepository.findByOwnerId(ownerId)).thenReturn(itemList);
        when(bookingRepository.findNearestPastBooking(1)).thenReturn(Collections.singletonList(pastBooking));
        when(bookingRepository.findNearestFutureBooking(1)).thenReturn(Collections.singletonList(nextBooking));
        when(bookingRepository.findNearestPastBooking(2)).thenReturn(Collections.singletonList(pastBooking));
        when(bookingRepository.findNearestFutureBooking(2)).thenReturn(Collections.singletonList(nextBooking));

        // Вызываем метод и проверяем результат
        List<ItemDtoWithBookings> resultItemList = itemService.getAllItemsByOwner(ownerId, from, size);

        assertNotNull(resultItemList);
        assertEquals(2, resultItemList.size());
    }

    @Test
    void testAddCommentToItem() {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1);
        commentDto.setItemId(1);
        commentDto.setText("Test Comment");

        Booking booking = new Booking();
        booking.setId(1);
        booking.setEnd(LocalDateTime.now().minusMinutes(1));
        User user = new User("User", "user@example.com");
        user.setId(1);
        booking.setBooker(user);
        Item item = new Item("Item 1", "Item 1 Description", true);
        item.setId(1);
        booking.setItem(item);
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);

        when(bookingRepository.findBookingsByBooker_Id(1)).thenReturn(bookingList);
        when(commentMapper.toComment(commentDto)).thenReturn(new Comment());
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment());
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(new CommentDto());

        CommentDto resultCommentDto = itemService.addCommentToItem(commentDto);

        assertNotNull(resultCommentDto);
    }

    @Test
    void testAddCommentToItemWithEmptyText() {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1);
        commentDto.setItemId(1);
        commentDto.setText(""); // Пустой комментарий

        assertThrows(ValidationException.class, () -> itemService.addCommentToItem(commentDto));
    }

    @Test
    void testAddCommentToItemWithNoValidBookings() {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1);
        commentDto.setItemId(1);
        commentDto.setText("Test Comment");

        when(bookingRepository.findBookingsByBooker_Id(1)).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.addCommentToItem(commentDto));
    }

    @Test
    void testSearchItems() {
        String searchText = "Test";
        List<Item> searchResults = new ArrayList<>();
        searchResults.add(new Item("Test Item 1", "Item Description", true));
        searchResults.add(new Item("Item 2", "Test Description", true));

        when(itemRepository.search(searchText)).thenReturn(searchResults);

        List<ItemDto> searchItems = itemService.searchItems(searchText);

        assertNotNull(searchItems);
        assertEquals(2, searchItems.size());
    }

    @Test
    void testSearchItemsWithEmptyText() {
        String searchText = "";

        List<ItemDto> searchItems = itemService.searchItems(searchText);

        assertNotNull(searchItems);
        assertTrue(searchItems.isEmpty());
    }
}
