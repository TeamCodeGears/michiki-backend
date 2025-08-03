package com.michiki.michiki.plan.service;

import com.michiki.michiki.common.exception.MemberNotFoundException;
import com.michiki.michiki.common.exception.NotParticipatingMemberException;
import com.michiki.michiki.common.exception.PlanNotFoundException;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.repository.MemberRepository;
import com.michiki.michiki.pivot.entity.MemberPlan;
import com.michiki.michiki.pivot.entity.repository.MemberPlanRepository;
import com.michiki.michiki.plan.dto.MemberOnlineStatusDto;
import com.michiki.michiki.plan.dto.PlanDetailResponseDto;
import com.michiki.michiki.plan.dto.PlanResponseDto;
import com.michiki.michiki.plan.entity.Plan;
import com.michiki.michiki.plan.repository.PlanRepository;
import com.michiki.michiki.place.dto.PlaceResponseDto;
import com.michiki.michiki.place.entity.Place;
import com.michiki.michiki.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PlanService {

    private final MemberPlanRepository memberPlanRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final PlaceRepository placeRepository;

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
        MemberPlan targetPlan = plan.getMemberPlans().stream()
                .filter(mp -> mp.getMember().getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new NotParticipatingMemberException("해당 계획에 참여중이 아닙니다."));
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

        MemberPlan memberPlan = memberPlanRepository.findByMemberAndPlan(member, plan)
                .orElseThrow(() -> new NotParticipatingMemberException("해당 계획에 참여중이 아닙니다."));
        memberPlan.changeColor(newColor);
    }

    public List<MemberOnlineStatusDto> getOnlineMembers(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("해당 계획을 찾을 수 없습니다."));

        List<Member> members = plan.getMemberPlans().stream()
                .map(MemberPlan::getMember)
                .toList();

        return members.stream()
                .map(member -> {
                    boolean isOnline = checkOnlineStatus(member.getMemberId());
                    return new MemberOnlineStatusDto(
                            member.getMemberId(),
                            member.getNickname(),
                            member.getProfileImage(),
                            isOnline
                    );
                })
                .collect(Collectors.toList());
    }

    private boolean checkOnlineStatus(Long memberId) {
        // TODO: 실제 구현 필요
        return false;
    }

    @Transactional(readOnly = true)
    public PlanDetailResponseDto getPlanDetail(Long planId, String username) {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new MemberNotFoundException("해당 사용자를 찾을 수 없습니다."));

        Plan plan = getPlan(planId);

        List<Place> places = placeRepository.findByPlanOrderByTravelDateAscOrderInDayAsc(plan);

        List<PlaceResponseDto> placeDtos = places.stream()
                .map(place -> PlaceResponseDto.builder()
                        .memberId(place.getMember().getMemberId())
                        .name(place.getName())
                        .description(place.getDescription())
                        .latitude(place.getLatitude())
                        .longitude(place.getLongitude())
                        .googlePlaceId(place.getGooglePlaceId())
                        .travelDate(place.getTravelDate())
                        .orderInDay(place.getOrderInDay())
                        .build())
                .toList();

        return PlanDetailResponseDto.builder()
                .title(plan.getTitle())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .places(placeDtos)
                .build();
    }

    // 공유 URI 생성
    @Transactional
    public String generateShareUri(Long planId, String username) {
        Plan plan = getPlan(planId);

        boolean isParticipant = plan.getMemberPlans().stream()
                .anyMatch(mp -> mp.getMember().getEmail().equals(username));

        if (!isParticipant) {
            throw new RuntimeException("공유 권한이 없습니다.");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        plan.updateShareURI(token, expiresAt);
        planRepository.save(plan);

        return "https://your-domain.com/share/" + token;
    }

    // 공유 URI 취소
    @Transactional
    public void cancelShareUri(Long planId, String username) {
        Plan plan = getPlan(planId);

        boolean isParticipant = plan.getMemberPlans().stream()
                .anyMatch(mp -> mp.getMember().getEmail().equals(username));

        if (!isParticipant) {
            throw new RuntimeException("공유 취소 권한이 없습니다.");
        }

        plan.clearShareURI();
        planRepository.save(plan);
    }

    // 공유 URI 상태 조회
    @Transactional(readOnly = true)
    public Map<String, Object> getShareStatus(Long planId, String username) {
        Plan plan = getPlan(planId);

        boolean isParticipant = plan.getMemberPlans().stream()
                .anyMatch(mp -> mp.getMember().getEmail().equals(username));

        if (!isParticipant) {
            throw new RuntimeException("공유 상태를 조회할 권한이 없습니다.");
        }

        boolean isShared = plan.getShareURI() != null && plan.getShareUriExpiresAt() != null;
        return Map.of(
                "isShared", isShared,
                "expiresAt", plan.getShareUriExpiresAt()
        );
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 사용자입니다."));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("해당 계획을 찾을 수 없습니다."));
    }
}
