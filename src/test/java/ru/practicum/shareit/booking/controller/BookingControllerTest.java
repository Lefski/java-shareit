package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController controller;
    private MockMvc mvc;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(controller)
                .build();
        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Item 1 description");
        User booker = new User();
        booker.setId(1);
        booker.setName("Booker 1");
        booker.setEmail("Bookeremail@email.com");
        bookingDto = new BookingDto(
                1,
                item,
                booker,
                BookingStatus.WAITING
        );


    }

    @Test
    void testAddBooking() throws Exception {
        when(bookingService.addBooking(any(BookingDto.class), any(Integer.class)))
                .thenReturn(bookingDto);

        String bookingDtoJson = mapper.writeValueAsString(bookingDto);

        mvc.perform(post("/bookings")
                        .content(bookingDtoJson)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(bookingDtoJson)); // Возвращаем JSON объекта bookingDto
    }


    @Test
    void testApproveOrRejectBooking() throws Exception {
        when(bookingService.approveOrRejectBooking(any(Integer.class), any(Integer.class), any(Boolean.class)))
                .thenReturn(bookingDto);

        // Выполняем PATCH запрос, используя параметры из bookingDto
        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .param("approved", Boolean.toString(true))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingDto.getBooker().getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void testGetBookingDetails() throws Exception {
        when(bookingService.getBookingById(any(Integer.class))).thenReturn(bookingDto);
        when(bookingService.isBookingOwner(any(Integer.class), any(Integer.class))).thenReturn(true);
        // Выполняем GET запрос, используя параметры из bookingDto
        mvc.perform(get("/bookings/{1}", bookingDto.getId())
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingDto.getBooker().getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void testGetOwnerBookings() throws Exception {
        when(bookingService.getOwnerBookings(any(String.class), any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Collections.singletonList(bookingDto));

        // Выполняем GET запрос, используя параметры из bookingDto
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingDto.getBooker().getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Collections.singletonList(bookingDto))));
    }

    @Test
    void testGetUserBookings() throws Exception {
        when(bookingService.getUserBookings(any(String.class), any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Collections.singletonList(bookingDto));

        // Выполняем GET запрос, используя параметры из bookingDto
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingDto.getBooker().getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Collections.singletonList(bookingDto))));
    }


}
