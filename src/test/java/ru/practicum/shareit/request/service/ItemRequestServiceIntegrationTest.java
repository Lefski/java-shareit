//package ru.practicum.shareit.request.service;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import ru.practicum.shareit.request.dto.ItemRequestDto;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@DataJpaTest
//public class ItemRequestServiceIntegrationTest {
//
//    @Autowired
//    private ItemRequestService itemRequestService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    public void testGetAllItemRequests() {
//        // Создайте тестового пользователя
//        User user = new User("Test User", "test@example.com");
//        user = userRepository.save(user);
//
//        // Создайте несколько тестовых запросов на предметы от этого пользователя
//        ItemRequestDto request1 = new ItemRequestDto(1, "Request 1", user, LocalDateTime.now());
//        ItemRequestDto request2 = new ItemRequestDto(2, "Request 2", user, LocalDateTime.now());
//        itemRequestService.createItemRequest(request1, user.getId());
//        itemRequestService.createItemRequest(request2, user.getId());
//
//        // Вызовите метод getAllItemRequests для пользователя
//        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequests(user.getId(), 0, 10);
//
//        // Проверьте, что список запросов не пустой и содержит ожидаемое количество элементов
//        assertNotNull(itemRequests);
//        assertEquals(2, itemRequests.size());
//
//        // Проверьте, что запросы принадлежат указанному пользователю
//        for (ItemRequestDto itemRequest : itemRequests) {
//            assertEquals(user.getId(), itemRequest.getRequestor().getId());
//        }
//    }
//}
