package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDtoWithBookingsTest {

    @Test
    void testSerializeItemDtoWithBookingsToJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings(1, "Test Item", "Item Description", true);

        String json = objectMapper.writeValueAsString(itemDtoWithBookings);

        assertEquals("{\"lastBooking\":null,\"nextBooking\":null,\"id\":1,\"name\":\"Test Item\",\"description\":\"Item Description\",\"available\":true,\"requestId\":null,\"comments\":null}", json);
    }

    @Test
    void testDeserializeJsonToItemDtoWithBookings() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Item Description\",\"available\":true,\"requestId\":null,\"comments\":null,\"lastBooking\":null,\"nextBooking\":null}";

        ItemDtoWithBookings itemDtoWithBookings = objectMapper.readValue(json, ItemDtoWithBookings.class);

        assertEquals(1, itemDtoWithBookings.getId());
        assertEquals("Test Item", itemDtoWithBookings.getName());
        assertEquals("Item Description", itemDtoWithBookings.getDescription());
        assertEquals(true, itemDtoWithBookings.getAvailable());
    }
}
