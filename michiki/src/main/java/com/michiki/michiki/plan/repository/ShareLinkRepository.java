package com.michiki.michiki.plan.repository;

import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.plan.entity.Plan;
import com.michiki.michiki.plan.entity.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

// 공유 URI 관리용 JPA 리포지토리
public interface ShareLinkRepository extends JpaRepository<ShareLink, Long> {

    // 24시간 내 유효한 URI만 조회 (계획 + 작성자 기준)
    @Query("SELECT s FROM ShareLink s " +
            "WHERE s.plan = :plan " +
            "AND s.createdBy = :member " +
            "AND s.revoked = false " +
            "AND s.shareUriExpiresAt > :now")
    Optional<ShareLink> findValidByPlanAndCreatedBy(
            @Param("plan") Plan plan,
            @Param("member") Member member,
            @Param("now") LocalDateTime now
    );

    // 유효기간이 지난 URI 일괄 삭제
    void deleteByShareUriExpiresAtBefore(LocalDateTime cutoff);
}
