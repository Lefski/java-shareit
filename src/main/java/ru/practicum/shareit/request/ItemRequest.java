package ru.practicum.shareit.request;

import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
public class ItemRequest {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "description")
    private String description;

    @JoinColumn(name = "requestor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User requestor;

    @Column(name = "created")
    private LocalDateTime created;
}
