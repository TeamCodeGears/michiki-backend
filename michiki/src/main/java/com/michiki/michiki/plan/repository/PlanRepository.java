package com.michiki.michiki.plan.repository;

import com.michiki.michiki.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    @Query("select p from Plan p join p.memberPlans mp where mp.member.memberId" +
            " = :memberId and p.startDate >= :jan1 and p.startDate <= :dec31")
    List<Plan> findAllByMemberIdAndStartDateInYear(
            @Param("memberId") Long memberId,
            @Param("jan1") LocalDate jan1,
            @Param("dec31") LocalDate dec31
    );
}

