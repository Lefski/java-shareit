package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentDtoTest {

    @Test
    void testSerializeCommentDtoToJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CommentDto commentDto = new CommentDto(1, "Test Comment", null, null, null, null);

        String json = objectMapper.writeValueAsString(commentDto);

        assertEquals("{\"id\":1,\"text\":\"Test Comment\",\"item\":null,\"author\":null,\"authorId\":null,\"itemId\":null,\"created\":null,\"authorName\":null}", json);
    }

    @Test
    void testDeserializeJsonToCommentDto() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"id\":1,\"text\":\"Test Comment\",\"item\":null,\"author\":null,\"created\":null,\"authorName\":null}";

        CommentDto commentDto = objectMapper.readValue(json, CommentDto.class);

        assertEquals(1, commentDto.getId());
        assertEquals("Test Comment", commentDto.getText());
    }
}
