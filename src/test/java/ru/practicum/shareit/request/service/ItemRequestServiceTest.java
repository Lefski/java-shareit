package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class ItemRequestServiceTest {

    ItemRequestDto itemRequestDto;
    User user;
    @InjectMocks
    private ItemRequestService itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("User 1", "user@email.com");
        user.setId(1);
        itemRequestDto = new ItemRequestDto(1, "Request 1", user, LocalDateTime.now());
    }

    @Test
    void testCreateItemRequest() {
        Integer userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(ItemRequestMapper.toItemRequest(itemRequestDto, user));

        ItemRequestDto savedItemRequest = itemRequestService.createItemRequest(itemRequestDto, userId);
        assertNotNull(savedItemRequest);
        assertEquals(savedItemRequest.getCreated(), itemRequestDto.getCreated());
        assertEquals(savedItemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(savedItemRequest.getRequestor(), itemRequestDto.getRequestor());
    }

    @Test
    void testCreateItemRequestWithInvalidDescription() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Integer userId = 1;

        assertThrows(ValidationException.class, () -> itemRequestService.createItemRequest(itemRequestDto, userId));
    }

    @Test
    void testGetAllItemRequestsByOwner() {
        Integer userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        ItemRequest mockItemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        when(itemRequestRepository.findAllByRequestorId(userId, Sort.by(Sort.Direction.DESC, "created")))
                .thenReturn(Collections.singletonList(mockItemRequest));

        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequestsByOwner(userId);

        assertFalse(itemRequests.isEmpty());
        ItemRequestDto resultItemRequest = itemRequests.get(0);
        assertEquals(mockItemRequest.getId(), resultItemRequest.getId());
        assertEquals(mockItemRequest.getDescription(), resultItemRequest.getDescription());
        assertEquals(mockItemRequest.getRequestor(), resultItemRequest.getRequestor());
    }

    @Test
    void testGetAllItemRequests() {
        Integer userId = 2;//должно отличаться от requestor id
        Integer offset = 0;
        Integer pageSize = 20;


        when(itemRequestRepository.findAll(PageRequest.of(offset, pageSize, Sort.Direction.DESC, "created")))
                .thenReturn(new PageImpl<>(Collections.singletonList(ItemRequestMapper.toItemRequest(itemRequestDto, user))));

        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequests(userId, offset, pageSize);
        ItemRequestDto mockItemRequest = itemRequests.get(0);
        assertFalse(itemRequests.isEmpty());
        ItemRequestDto resultItemRequest = itemRequests.get(0);
        assertEquals(mockItemRequest.getId(), resultItemRequest.getId());
        assertEquals(mockItemRequest.getDescription(), resultItemRequest.getDescription());
        assertEquals(mockItemRequest.getRequestor(), resultItemRequest.getRequestor());
    }

    @Test
    void testGetRequestById() {
        Integer requestId = 1;
        Integer userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        ItemRequest mockItemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(mockItemRequest));

        ItemRequestDto itemRequest = itemRequestService.getRequestById(requestId, userId);

        assertNotNull(itemRequest);

        assertEquals(mockItemRequest.getId(), itemRequest.getId());
        assertEquals(mockItemRequest.getDescription(), itemRequest.getDescription());
        assertEquals(mockItemRequest.getRequestor(), itemRequest.getRequestor());
    }

    @Test
    void testGetRequestByIdWithInvalidUser() {
        Integer requestId = 1;
        Integer userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(requestId, userId));
    }

    @Test
    void testGetRequestByIdWithInvalidRequest() {
        Integer requestId = 1;
        Integer userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(requestId, userId));
    }


}
