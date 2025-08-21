package com.michiki.michiki.plan.repository;

import com.michiki.michiki.plan.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 유저가 받은 알림을 최신순으로 가져옴
    Page<Notification> findByReceiverMemberIdOrderByIdDesc(Long receiverMemberId, Pageable pageable);

    List<Notification> findByReceiverMemberIdAndIsReadFalse(Long memberId);

    List<Notification> findByReceiverMemberIdOrderByCreatedAtDesc(Long receiverMemberId);


}
