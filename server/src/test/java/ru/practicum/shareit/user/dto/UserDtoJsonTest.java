package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoJsonTest {

    @Test
    void testSerializeUserDtoToJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto userDto = new UserDto(1, "Test User", "test@example.com");

        String json = objectMapper.writeValueAsString(userDto);

        assertEquals("{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\"}", json);
    }

    @Test
    void testDeserializeJsonToUserDto() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertEquals(1, userDto.getId());
        assertEquals("Test User", userDto.getName());
        assertEquals("test@example.com", userDto.getEmail());
    }
}
