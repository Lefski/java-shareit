package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Integer id;
    @NotBlank
    private String text;
    private Integer authorId;
    private Integer itemId;
    private LocalDateTime created;
    private String authorName;

    public CommentDto() {
    }


}
