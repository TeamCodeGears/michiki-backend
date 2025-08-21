package com.michiki.michiki.plan.service;


import com.michiki.michiki.plan.dto.NotificationDto;
import com.michiki.michiki.plan.entity.Notification;
import com.michiki.michiki.plan.entity.NotificationType;
import com.michiki.michiki.plan.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;


    private void createAndSend(
            Long receiverId,
            Long planId,
            String planTitle,
            String actorNickname,
            NotificationType type
    ){
        Notification notification = Notification.builder()
                .receiverMemberId(receiverId)
                .planId(planId)
                .planTitle(planTitle)
                .actorNickname(actorNickname)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // 실시간 웹소켓으로 알림 보내기 (벨 표시용)
        messagingTemplate.convertAndSend(
                "/sub/member/" + receiverId + "/notifications",
                NotificationDto.from(notification)
        );
    }

    // 누군가 참여했을 때 (본인 제외한 다른 멤버에게)
    public void notifyJoined(
            Long planId,
            String planTitle,
            Long actorId,
            String actorNickname,
            Collection<Long> otherMemberIds
    ) {
        for (Long receiverId : otherMemberIds) {
            if (!receiverId.equals(actorId)) {
                createAndSend(receiverId, planId, planTitle, actorNickname, NotificationType.PLAN_JOINED);
            }
        }
    }

    // 누군가 나갔을 때
    public void notifyLeft(
            Long planId,
            String planTitle,
            Long actorId,
            String actorNickname,
            Collection<Long> remainingMemberIds
    ) {
        for (Long receiverId : remainingMemberIds) {
            if (!receiverId.equals(actorId)) {
                createAndSend(receiverId, planId, planTitle, actorNickname, NotificationType.PLAN_LEFT);
            }
        }
    }

    // 계획이 삭제됐을 때 (모든 멤버에게)
    public void notifyDeleted(
            Long planId,
            String planTitle,
            Collection<Long> allMemberIds
    ) {
        for (Long receiverId : allMemberIds) {
            createAndSend(receiverId, planId, planTitle, null, NotificationType.PLAN_DELETED);
        }
    }

    public void notifyPlaceChanged(Long planId) {
        String destination = "/sub/plans/" + planId + "/place-changed";
        messagingTemplate.convertAndSend(destination, "changed");
    }

    @Transactional
    public void markAllAsRead(Long memberId) {
        List<Notification> unreadList = notificationRepository.findByReceiverMemberIdAndIsReadFalse(memberId);
        for (Notification n : unreadList) {
            n.markAsRead();
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getAllNotifications(Long memberId) {
        return notificationRepository.findByReceiverMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(NotificationDto::from)
                .toList();
    }


}

