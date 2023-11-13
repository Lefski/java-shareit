package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingDtoSerializationTest {

    @Test
    void testSerializeBookingDtoToJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingDto bookingDto = new BookingDto(1, null, null, null, null, BookingStatus.WAITING);

        String json = objectMapper.writeValueAsString(bookingDto);

        String expectedJson = "{\"id\":1,\"itemId\":null,\"bookerId\":null,\"start\":null,\"end\":null,\"item\":null,\"booker\":null,\"status\":\"WAITING\"}";

        assertEquals(expectedJson, json);
    }

    @Test
    void testDeserializeJsonToBookingDto() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"id\":1,\"itemId\":null,\"bookerId\":null,\"item\":null,\"booker\":null,\"status\":\"WAITING\"}";

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        assertEquals(1, bookingDto.getId());
        assertNull(bookingDto.getItemId());
        assertNull(bookingDto.getBookerId());
        assertNull(bookingDto.getItem());
        assertNull(bookingDto.getBooker());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertNull(bookingDto.getStart());
        assertNull(bookingDto.getEnd());
    }
}
