package com.michiki.michiki.plan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long receiverMemberId;
    private Long planId;
    private String planTitle;
    private String actorNickname;

    @Enumerated(STRING)
    private NotificationType type;
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isRead = false;

    public void markAsRead() {
        this.isRead = true;
    }
}





