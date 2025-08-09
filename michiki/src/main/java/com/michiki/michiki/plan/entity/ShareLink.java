package com.michiki.michiki.plan.entity;

import com.michiki.michiki.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// 공유 URI 정보 관리 엔티티

@Entity
@Table(name = "share_links")
@Getter
@SequenceGenerator(
        name = "share_links_seq_gen",
        sequenceName = "SHARE_LINKS_SEQ",
        allocationSize = 1
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareLink {

    // 공유 URI 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "share_links_seq_gen")
    private Long id;

    // 공유 대상이 되는 계획
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAN_ID", nullable = false)
    private Plan plan;

    // 공유 URI를 생성한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY", nullable = false)
    private Member createdBy;

    // 공유 URI (랜덤 토큰)
    @Column(name = "SHARE_URI", nullable = false, unique = true, length = 255)
    private String shareURI;

    // 공유 URI 생성 시간
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    // 공유 URI 만료 시간
    @Column(name = "SHARE_URI_EXPIRES_AT", nullable = false)
    private LocalDateTime shareUriExpiresAt;

    // 수동 취소 여부
    @Column(name = "REVOKED", nullable = false)
    private boolean revoked;

    // URI 만료 여부
    public boolean isExpired() {
        return shareUriExpiresAt.isBefore(LocalDateTime.now());
    }

    // URI가 유효한 상태인지 여부
    public boolean isActive() {
        return !revoked && !isExpired();
    }
}
