package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "comment")
    String text;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.EAGER)
    Item item;

    @JoinColumn(name = "author_id")
    @ManyToOne(fetch = FetchType.EAGER)
    User author;

    @Column(name = "created")
    LocalDateTime created;


    public Comment(String text, Item item, User author, LocalDateTime created) {
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
    }

    public Comment() {
    }
}
