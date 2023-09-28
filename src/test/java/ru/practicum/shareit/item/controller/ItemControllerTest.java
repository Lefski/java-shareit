package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemServiceImpl itemService;

    private MockMvc mockMvc;

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(itemController)
                .build();
    }

    @Test
    public void testAddItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("This is a test item.");

        when(itemService.addItem(any(ItemDto.class), anyInt())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(asJsonString(itemDto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("This is a test item."));
    }

    @Test
    public void testEditItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("This item has been updated.");

        when(itemService.editItem(anyInt(), any(ItemDto.class), anyInt())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .content(asJsonString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("This item has been updated."));
    }

    @Test
    public void testGetItemById() throws Exception {
        ItemDtoWithBookings itemDto = new ItemDtoWithBookings();
        itemDto.setName("Test Item");
        itemDto.setDescription("This is a test item.");

        when(itemService.getItemById(anyInt(), anyInt())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("This is a test item."));
    }

    @Test
    public void testGetAllItemsByOwner() throws Exception {
        List<ItemDtoWithBookings> items = Arrays.asList(
                new ItemDtoWithBookings("Item 1", "Description 1"),
                new ItemDtoWithBookings("Item 2", "Description 2")
        );

        when(itemService.getAllItemsByOwner(anyInt(), anyInt(), anyInt())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].name").value("Item 2"));
    }

    @Test
    public void testSearchItems() throws Exception {
        List<ItemDto> items = Arrays.asList(
                new ItemDto("Item 1", "Description 1"),
                new ItemDto("Item 2", "Description 2")
        );

        when(itemService.searchItems(anyString(), anyInt(), anyInt())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "test")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].name").value("Item 2"));
    }

    @Test
    public void testAddCommentToItem() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("This is a comment.");

        when(itemService.addCommentToItem(any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(asJsonString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("This is a comment."));
    }
}
