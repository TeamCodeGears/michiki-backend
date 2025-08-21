package com.michiki.michiki.plan.dto;


import com.michiki.michiki.plan.entity.Notification;
import com.michiki.michiki.plan.entity.NotificationType;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationDto {
    private Long id;
    private NotificationType type;
    private Long planId;
    private String planTitle;
    private String actorNickname;
    private LocalDateTime createdAt;

public static NotificationDto from(Notification n) {
    return NotificationDto.builder()
            .id(n.getId())
            .type(n.getType())
            .planId(n.getPlanId())
            .planTitle(n.getPlanTitle())
            .actorNickname(n.getActorNickname())
            .createdAt(n.getCreatedAt())
            .build();
    }
}