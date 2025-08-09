package com.michiki.michiki.plan.service;

import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.repository.MemberRepository;
import com.michiki.michiki.plan.entity.Plan;
import com.michiki.michiki.plan.entity.ShareLink;
import com.michiki.michiki.plan.repository.PlanRepository;
import com.michiki.michiki.plan.repository.ShareLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

// 공유 URI 생성을 담당하는 서비스 클래스

@Service
@RequiredArgsConstructor
public class ShareLinkService {

    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final ShareLinkRepository shareLinkRepository;

    // 공유 URI 생성 또는 재사용

    @Transactional
    public String createOrReuseShareUri(Long planId, String username) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 계획을 찾을 수 없습니다."));

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));


        // 기존에 24시간 내 유효한 URI가 있으면 재사용
        return shareLinkRepository
                .findValidByPlanAndCreatedBy(plan, member, LocalDateTime.now())
                .map(ShareLink::getShareURI)
                .orElseGet(() -> {
                    // 새 URI 생성
                    String newUri = UUID.randomUUID().toString();

                    ShareLink link = ShareLink.builder()
                            .plan(plan)
                            .createdBy(member)
                            .shareURI(newUri)
                            .createdAt(LocalDateTime.now())
                            .shareUriExpiresAt(LocalDateTime.now().plusHours(24))
                            .revoked(false)
                            .build();

                    shareLinkRepository.save(link);
                    return newUri;
                });
    }
}
