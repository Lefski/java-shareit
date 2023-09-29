package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    public void testFindByOwnerId() {
        // Создаем пользователя и сохраняем его в базе данных
        User user = new User("Test User", "test@example.com");
        userRepository.save(user);

        // Создаем несколько предметов и связываем их с пользователем
        Item item1 = new Item("Item 1", "Item Description 1", true);
        item1.setOwner(user);
        Item item2 = new Item("Item 2", "Item Description 2", true);
        item2.setOwner(user);

        // Сохраняем предметы в базе данных
        itemRepository.save(item1);
        itemRepository.save(item2);

        // Вызываем метод findByOwnerId и проверяем результат
        List<Item> items = itemRepository.findByOwnerId(user.getId());

        assertNotNull(items);
        assertEquals(2, items.size());

        for (Item item : items) {
            assertEquals(user.getId(), item.getOwner().getId());
        }
    }


    @Test
    public void testSearch() {
        // Создаем несколько предметов с разными именами и описаниями
        Item item1 = new Item("Test Item 1", "Item Description 1", true);
        Item item2 = new Item("Item 2", "Description 2", true);
        Item item3 = new Item("Test Item 3", "Item Description 3", true);

        // Сохраняем предметы в базе данных
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        // Вызываем метод search с текстом поиска и проверяем результат
        List<Item> searchResults = itemRepository.search("Test");

        assertNotNull(searchResults);
        assertEquals(2, searchResults.size());

        // Проверяем, что найденные предметы содержат текст "Test"
        for (Item item : searchResults) {
            assertTrue(item.getName().contains("Test") || item.getDescription().contains("Test"));
        }
    }
}
