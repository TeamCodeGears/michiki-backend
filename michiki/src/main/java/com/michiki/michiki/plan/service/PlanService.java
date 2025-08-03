package com.michiki.michiki.plan.service;

import com.michiki.michiki.common.exception.MemberNotFoundException;
import com.michiki.michiki.common.exception.NotParticipatingMemberException;
import com.michiki.michiki.common.exception.PlanNotFoundException;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.repository.MemberRepository;
import com.michiki.michiki.pivot.entity.MemberPlan;
import com.michiki.michiki.pivot.entity.repository.MemberPlanRepository;
import com.michiki.michiki.plan.dto.PlanResponseDto;
import com.michiki.michiki.plan.entity.Plan;
import com.michiki.michiki.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service

public class PlanService {

    private final MemberPlanRepository memberPlanRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    @Transactional(readOnly = true)
    public List<PlanResponseDto> getPlansStartInYear(Long memberId, int year) {
        Member member = getMember(memberId);
        LocalDate jan1 = LocalDate.of(year, 1, 1);
        LocalDate dec31 = LocalDate.of(year, 12, 31);

        List<Plan> plans = planRepository.findAllByMemberIdAndStartDateInYear(member.getMemberId(), jan1, dec31);
        return plans.stream().map(PlanResponseDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public String leavePlan(Long memberId, Long planId) {
        Plan plan = getPlan(planId);
        MemberPlan targetPlan = plan.getMemberPlans().stream().
                filter(mp -> mp.getMember().getMemberId().equals(memberId)).
                findFirst().orElseThrow(() ->
                        new NotParticipatingMemberException("해당 계획에 참여중이 아닙니다."));
        plan.getMemberPlans().remove(targetPlan);

        if (plan.getMemberPlans().isEmpty()) {
            planRepository.delete(plan);
            return "삭제";
        }
        return "나가기";
    }

    @Transactional
    public void changeColor(Long memberId, Long planId, String newColor) {
        Member member = getMember(memberId);
        Plan plan = getPlan(planId);

        MemberPlan memberPlan = memberPlanRepository.findByMemberAndPlan(member, plan).
                orElseThrow(() -> new NotParticipatingMemberException("해당 계획에 참여중이 아닙니다."));
        memberPlan.changeColor(newColor);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 사용자입니다."));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("존재하지 않는 계획입니다."));
    }
}
