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
import com.michiki.michiki.plan.dto.PlanRequestDto;
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
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PlanService {

    private final MemberPlanRepository memberPlanRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final PlaceRepository placeRepository;

    private static final Random random = new Random();

    // 플랜 생성
    @Transactional
    public void createPlan(Long memberId, PlanRequestDto planRequestDto) {

        Member member = getMember(memberId);

        Plan plan = Plan.builder()
                .title(planRequestDto.getTitle())
                .startDate(planRequestDto.getStartDate())
                .endDate(planRequestDto.getEndDate())
                .shareURI(null)
                .shareUriExpiresAt(null)
                .build();

        String color = getRandomHexColor();

        plan.getMemberPlans().add(new MemberPlan(member, plan,color));

        planRepository.save(plan);
    }

    // 특정 연도에 시작된 사용자의 모든 여행 계획 조회
    @Transactional(readOnly = true)
    public List<PlanResponseDto> getPlansStartInYear(Long memberId, int year) {
        Member member = getMember(memberId);
        LocalDate jan1 = LocalDate.of(year, 1, 1);
        LocalDate dec31 = LocalDate.of(year, 12, 31);
        List<Plan> plans = planRepository.findAllByMemberIdAndStartDateInYear(member.getMemberId(), jan1, dec31);
        return plans.stream().map(PlanResponseDto::fromEntity).collect(Collectors.toList());
    }

    // 사용자가 여행 계획에서 나가기 (마지막 사용자는 계획 삭제)
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

    // 계획 내 사용자 색상 변경
    @Transactional
    public void changeColor(Long memberId, Long planId, String newColor) {
        Member member = getMember(memberId);
        Plan plan = getPlan(planId);
        MemberPlan memberPlan = memberPlanRepository.findByMemberAndPlan(member, plan)
                .orElseThrow(() -> new NotParticipatingMemberException("해당 계획에 참여중이 아닙니다."));
        memberPlan.changeColor(newColor);
    }

    @Transactional(readOnly = true)
    // 계획에 참여중인 사용자의 온라인 상태 목록 반환
    public List<MemberOnlineStatusDto> getOnlineMembers(Long planId, String username) {
        Plan plan = getPlan(planId);
        List<Member> members = plan.getMemberPlans().stream()
                .map(MemberPlan::getMember)
                .toList();
        return members.stream()
                .map(member -> new MemberOnlineStatusDto(
                        member.getMemberId(),
                        member.getNickname(),
                        member.getProfileImage(),
                        checkOnlineStatus(member.getMemberId())
                ))
                .collect(Collectors.toList());
    }

    // ToDo: 온라인 여부 확인 (추후 구현 예정)
    private boolean checkOnlineStatus(Long memberId) {
        return false;
    }

    // 여행 계획 상세 정보 조회
    @Transactional(readOnly = true)
    public PlanDetailResponseDto getPlanDetail(Long planId) {

        Plan plan = getPlan(planId);
        List<Place> places = placeRepository.findByPlanOrderByTravelDateAsc(plan);
        List<PlaceResponseDto> placeDtos = places.stream()
                .map(place -> PlaceResponseDto.builder()
                        .memberId(place.getMember().getMemberId())
                        .placeId(place.getPlaceId())
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
                .planId(plan.getPlanId())
                .title(plan.getTitle())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .places(placeDtos)
                .build();
    }

    // 사용자 참여 여부 검증
    private void validateParticipant(Plan plan, String username) {
        boolean isParticipant = plan.getMemberPlans().stream()
                .anyMatch(mp -> mp.getMember().getEmail().equals(username));
        if (!isParticipant) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }
    }

    // 사용자 조회
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 사용자입니다."));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("해당 계획을 찾을 수 없습니다."));
    }

    private String getRandomHexColor() {
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int x = random.nextInt(256);
        return String.format("#%02x%02x%02x", r, g, x);
    }
}
