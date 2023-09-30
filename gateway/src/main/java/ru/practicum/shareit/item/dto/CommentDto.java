package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Integer id;
    private String text;
    private Integer authorId;
    private Integer itemId;
    private LocalDateTime created;
    private String authorName;

    public CommentDto() {
    }


}
