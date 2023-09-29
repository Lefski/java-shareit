package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestDtoJsonTest {

    @Test
    void testSerializeItemRequestDtoToJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Request 1", null, null);

        String json = objectMapper.writeValueAsString(itemRequestDto);

        assertEquals("{\"id\":1,\"description\":\"Request 1\",\"requestor\":null,\"created\":null,\"items\":null}", json);
    }

    @Test
    void testDeserializeJsonToItemRequestDto() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"id\":1,\"description\":\"Request 1\",\"requestor\":null,\"created\":null,\"items\":null}";

        ItemRequestDto itemRequestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertEquals(1, itemRequestDto.getId());
        assertEquals("Request 1", itemRequestDto.getDescription());
    }
}
