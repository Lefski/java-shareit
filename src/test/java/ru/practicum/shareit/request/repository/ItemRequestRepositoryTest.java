package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindAllByRequestorId() {
        User user = new User("Test User", "test@example.com");
        user = userRepository.save(user);

        ItemRequest request1 = new ItemRequest("Request 1", LocalDateTime.now(), user);
        ItemRequest request2 = new ItemRequest("Request 2", LocalDateTime.now(), user);
        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(user.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertNotNull(requests);
        assertEquals(2, requests.size());

        for (ItemRequest request : requests) {
            assertEquals(user.getId(), request.getRequestor().getId());
        }
    }
}
