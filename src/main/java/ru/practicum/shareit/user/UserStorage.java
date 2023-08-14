package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    User deleteUserById(Integer id);

    User getUserById(Integer id);

    List<User> getUsers();
}
