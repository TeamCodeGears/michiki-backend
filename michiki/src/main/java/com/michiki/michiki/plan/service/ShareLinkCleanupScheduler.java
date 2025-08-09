package com.michiki.michiki.plan.service;

import com.michiki.michiki.plan.repository.ShareLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// 공유 URI 정리를 위한 스케줄러
@Component
@RequiredArgsConstructor
public class ShareLinkCleanupScheduler {

    private final ShareLinkRepository shareLinkRepository;

    // 매일 새벽 3시에 만료된 공유 URI 자동 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredShareLinks() {
        LocalDateTime now = LocalDateTime.now();
        shareLinkRepository.deleteByShareUriExpiresAtBefore(now);
    }
}