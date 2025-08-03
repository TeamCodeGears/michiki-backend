package com.michiki.michiki.plan.repository;

import com.michiki.michiki.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Plan entity를 위한 JPA Repository
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    // 특정 유저가 해당 연도에 생성한 여행 계획 목록 조회
    @Query("select p from Plan p join p.memberPlans mp where mp.member.memberId" +
            " = :memberId and p.startDate >= :jan1 and p.startDate <= :dec31")
    List<Plan> findAllByMemberIdAndStartDateInYear(
            @Param("memberId") Long memberId,
            @Param("jan1") LocalDate jan1,
            @Param("dec31") LocalDate dec31
    );

    // 공유 URI로 기반으로 계획 조회
   Optional<Plan> findByShareURI(String shareURI);
}

