package ru.practicum.shareit.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Service
@Data
@Slf4j
public class UserService {
    private final InMemoryUserStorage userStorage;

    public User createUser(User user) {
        validationUser(user);
        log.info("Выполнен запроc на создание пользователя");
        return userStorage.create(user);
    }

    public User getUserById(Integer id) {
        log.info("Выполнен запроc на получение пользователя по id");
        return userStorage.getUserById(id);
    }

    public User updateUser(Integer id, User updatedUser) {
        log.info("Выполнен запроc на обновление пользователя");
        updatedUser.setId(id);
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()) {
            validationEmail(updatedUser);
        }
        return userStorage.update(updatedUser);
    }


    private void validationUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            logValidationUser();
            throw new ValidationException("Некорректный адрес электронной почты", HttpStatus.BAD_REQUEST);
        } else if (user.getName() == null || user.getName().isBlank()) {
            logValidationUser();
            throw new ValidationException("Имени нет или оно пустое", HttpStatus.BAD_REQUEST);
        }
        validationEmail(user);
    }

    private void validationEmail(User user) {
        List<User> existingUsers = userStorage.getUsers();
        for (User existingUser : existingUsers) {
            if (existingUser.getEmail().equals(user.getEmail()) && existingUser.getId() != user.getId()) {
                logValidationUser();
                throw new ValidationException("Адрес электронной почты уже занят", HttpStatus.CONFLICT);
            }
        }
    }

    private void logValidationUser() {
        log.warn("Валидация пользователя не пройдена");
    }


    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public void deleteUser(Integer id) {
        userStorage.deleteUserById(id);
    }
}
