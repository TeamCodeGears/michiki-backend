package com.michiki.michiki.pivot.entity.repository;

import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.pivot.entity.MemberPlan;
import com.michiki.michiki.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberPlanRepository extends JpaRepository<MemberPlan, Long> {
    Optional<MemberPlan> findByMemberAndPlan(Member member, Plan plan);
}

