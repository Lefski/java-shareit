package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CommentMapperTest {

    @InjectMocks
    private CommentMapper commentMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testToCommentDto() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Test Comment");
        comment.setCreated(LocalDateTime.now());
        User user = new User();
        user.setName("Author 1");
        comment.setAuthor(user);
        Item item = new Item();
        comment.setItem(item);

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    public void testToComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");
        commentDto.setAuthorId(1);
        commentDto.setItemId(2);

        User author = new User();
        author.setId(1);
        Item item = new Item();
        item.setId(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(author));
        when(itemRepository.findById(2)).thenReturn(Optional.of(item));

        Comment comment = commentMapper.toComment(commentDto);

        assertNotNull(comment);
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(author, comment.getAuthor());
        assertEquals(item, comment.getItem());
    }

    @Test
    public void testToComment_WithNotFoundException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");
        commentDto.setAuthorId(1);
        commentDto.setItemId(2);

        when(userRepository.findById(1)).thenReturn(Optional.empty());
        when(itemRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentMapper.toComment(commentDto));
    }
}
