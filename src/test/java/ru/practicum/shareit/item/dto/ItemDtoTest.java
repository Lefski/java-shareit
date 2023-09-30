package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDtoTest {

    @Test
    void testSerializeItemDtoToJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ItemDto itemDto = new ItemDto(1, "Test Item", "Item Description", true);

        String json = objectMapper.writeValueAsString(itemDto);

        assertEquals("{\"id\":1,\"name\":\"Test Item\",\"description\":\"Item Description\",\"available\":true,\"requestId\":null,\"comments\":null,\"request\":null}", json);
    }

    @Test
    void testDeserializeJsonToItemDto() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Item Description\",\"available\":true,\"requestId\":null,\"comments\":null,\"request\":null}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertEquals(1, itemDto.getId());
        assertEquals("Test Item", itemDto.getName());
        assertEquals("Item Description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
    }
}
