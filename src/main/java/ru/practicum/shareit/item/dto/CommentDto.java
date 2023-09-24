package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Integer id;
    private String text;
    private Item item;
    private User author;
    private Integer authorId;
    private Integer itemId;
    private LocalDateTime created;
    private String authorName;

    public CommentDto() {
    }

    public CommentDto(String text, Item item, User author, LocalDateTime created, String authorName) {
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
        this.authorName = authorName;
    }

    public CommentDto(Integer id, String text, Item item, User author, LocalDateTime created, String authorName) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
        this.authorName = authorName;
    }
}
