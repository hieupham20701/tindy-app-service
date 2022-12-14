package com.tindy.app.model.entity;

import com.tindy.app.model.enums.ConversationStatus;
import com.tindy.app.model.enums.ConversationType;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "conversation")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    private Date createdAt;
    private Date updatedAt;
    private String avatar;
    @Enumerated(EnumType.STRING)
    private ConversationStatus status;
    @Enumerated(EnumType.STRING)
    private ConversationType type;
}
