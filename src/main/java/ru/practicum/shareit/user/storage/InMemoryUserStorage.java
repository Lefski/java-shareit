package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;


    @Override
    public User create(User user) {
        user.setId(idCounter);
        idCounter++;
        log.debug("Добавлен user: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("Обновлен user: {}", user);
        User oldUser = users.get(user.getId());
        if (user.getName() != null && !user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            oldUser.setEmail(user.getEmail());
        }
        users.put(user.getId(), oldUser);
        return oldUser;
    }

    @Override
    public User deleteUserById(Integer id) {
        User user = getUserById(id);
        log.debug("Удален user с id: {}", user);
        users.remove(id);
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователя с переданным id не существует", HttpStatus.NOT_FOUND);
        }
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        log.debug("Текущее кол-во пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }
}
