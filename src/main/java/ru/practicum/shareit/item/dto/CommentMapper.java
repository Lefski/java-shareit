package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class CommentMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public CommentDto toCommentDto(Comment comment){
        return new CommentDto(comment.getId(), comment.getComment(), comment.getItem(), comment.getAuthor(), comment.getCreated(),comment.getAuthor().getName());

    }

    public Comment toComment(CommentDto commentDto) {
        User author =  userRepository.findById(commentDto.getAuthorId()).orElseThrow(() -> new NotFoundException("Автора с переданным id не существует", HttpStatus.NOT_FOUND));
        Item item =  itemRepository.findById(commentDto.getItemId()).orElseThrow(() -> new NotFoundException("Автора с переданным id не существует", HttpStatus.NOT_FOUND));

        return new Comment(commentDto.getText(), item, author, LocalDateTime.now());
    }


}
