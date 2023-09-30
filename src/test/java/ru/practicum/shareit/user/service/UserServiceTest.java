package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto("Test User", "test@example.com");
        User user = UserMapper.toUser(userDto);

        when(userRepository.save(user)).thenReturn(user);

        UserDto savedUser = userService.createUser(userDto);

        assertNotNull(savedUser);
        assertEquals(userDto.getName(), savedUser.getName());
        assertEquals(userDto.getEmail(), savedUser.getEmail());
    }

    @Test
    void testCreateUserWithInvalidEmail() {
        UserDto userDto = new UserDto("Test User", "invalid_email");

        assertThrows(ValidationException.class, () -> userService.createUser(userDto));
    }

    @Test
    void testCreateUserWithInvalidEmailSymbols() {
        UserDto userDto = new UserDto("Test User", "test@@example.com");

        assertThrows(ValidationException.class, () -> userService.createUser(userDto));
    }

    @Test
    void testCreateUserWithBlankName() {
        UserDto userDto = new UserDto("", "test@example.com");

        assertThrows(ValidationException.class, () -> userService.createUser(userDto));
    }

    @Test
    void testGetUserById() {
        int userId = 1;
        User user = new User("Test User", "test@example.com");
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testGetUserByIdNotFound() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void testUpdateUser() {
        int userId = 1;
        UserDto updatedUserDto = new UserDto("Updated User", "updated@example.com");
        User existingUser = new User("Test User", "test@example.com");
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto updatedUser = userService.updateUser(userId, updatedUserDto);

        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals(updatedUserDto.getName(), updatedUser.getName());
        assertEquals(updatedUserDto.getEmail(), updatedUser.getEmail());
    }


    @Test
    void testGetUsers() {
        User user1 = new User("User 1", "user1@example.com");
        User user2 = new User("User 2", "user2@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> users = userService.getUsers();

        assertFalse(users.isEmpty());
        assertEquals(2, users.size());

        UserDto userDto1 = users.get(0);
        UserDto userDto2 = users.get(1);

        assertEquals(user1.getName(), userDto1.getName());
        assertEquals(user1.getEmail(), userDto1.getEmail());

        assertEquals(user2.getName(), userDto2.getName());
        assertEquals(user2.getEmail(), userDto2.getEmail());
    }

    @Test
    void testDeleteUser() {
        int userId = 1;
        User user = new User("Test User", "test@example.com");
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUserNotFound() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
    }
}
