package com.michiki.michiki.place.service;

import com.michiki.michiki.common.exception.MemberNotFoundException;
import com.michiki.michiki.common.exception.NotParticipatingMemberException;
import com.michiki.michiki.common.exception.PlaceNotFoundException;
import com.michiki.michiki.common.exception.PlanNotFoundException;
import com.michiki.michiki.member.repository.MemberRepository;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.place.dto.*;
import com.michiki.michiki.place.entity.Place;
import com.michiki.michiki.place.repository.PlaceRepository;
import com.michiki.michiki.plan.entity.Plan;
import com.michiki.michiki.plan.repository.PlanRepository;
import com.michiki.michiki.plan.service.NotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    // 장소 추가
    @Transactional
    public void addPlace(Long planId, Long memberId, PlaceRequestDto placeRequestDto) {
        Plan plan = getPlan(planId);
        Member member = getMember(memberId);

        memberValidate(memberId, plan);

        Place place = createPlace(placeRequestDto, plan, member);
        plan.getPlaces().add(place);

        placeRepository.save(place);
    }

    // 장소 설명 수정
    @Transactional
    public void updatePlace(Long memberId, Long planId, Long placeId, PlaceUpdateRequestDto dto) {
        Plan plan = getPlan(planId);
        Member member = getMember(memberId);

        memberValidate(member.getMemberId(), plan);

        Place place = getPlace(plan, placeId);

        place.changePlan(dto);

        notificationService.notifyPlaceChanged(plan.getPlanId());
    }

    // 장소 삭제
    @Transactional
    public void deletePlace(Long memberId, Long planId, Long placeId) {
        Member member = getMember(memberId);
        Plan plan = getPlan(planId);

        memberValidate(member.getMemberId(), plan);

        Place place = getPlace(plan, placeId);
        plan.getPlaces().remove(place);

        placeRepository.delete(place);

        notificationService.notifyPlaceChanged(plan.getPlanId());
    }

    // 장소 순서 정렬
    @Transactional
    public List<PlaceResponseDto> reorderPlaces(Long memberId, Long planId, PlaceReorderRequestDto dto) {
        Plan plan = getPlan(planId);

        memberValidate(memberId, plan);

        List<Place> placesOfDay = placeRepository.findByPlanAndTravelDate(plan, dto.getTravelDate());

        List<Place> updated = dto.getPlaces().stream()
                .map(o -> {
                    Place place = placesOfDay.stream()
                            .filter(p -> p.getPlaceId().equals(o.getPlaceId()))
                            .findFirst()
                            .orElseThrow(() -> new PlaceNotFoundException("해당 날짜에 장소가 없습니다."));
                    place.changeOrderInDay(o.getOrderInDay());
                    return place;
                })
                .toList();

        notificationService.notifyPlaceChanged(plan.getPlanId());

        return updated.stream()
                .sorted(Comparator.comparing(Place::getOrderInDay))
                .map(PlaceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 해당 유저가 해당 계획에 참여중인지 검증
    private static void memberValidate(Long memberId, Plan plan) {
        boolean participates = plan.getMemberPlans().stream()
                .anyMatch(mp -> mp.getMember().getMemberId().equals(memberId));
        if (!participates) {
            throw new NotParticipatingMemberException("해당 계획에 참여중이 아닙니다.");
        }
    }

    // place Entity 생성 헬퍼 메서드
    private static Place createPlace(PlaceRequestDto placeRequestDto, Plan plan, Member member) {
        return Place.builder()
                .plan(plan)
                .member(member)
                .name(placeRequestDto.getName())
                .description(placeRequestDto.getDescription())
                .latitude(placeRequestDto.getLatitude())
                .longitude(placeRequestDto.getLongitude())
                .googlePlaceId(placeRequestDto.getGooglePlaceId())
                .travelDate(placeRequestDto.getTravelDate())
                .orderInDay(placeRequestDto.getOrderInDay())
                .build();
    }

    // 유저 조회
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 사용자입니다."));
    }

    // 계획 조회
    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("존재하지 않는 계획입니다."));
    }

    // 계획 기준 장소 조회
    private Place getPlace(Plan plan, Long placeId) {
        return placeRepository.findByPlanAndPlaceId(plan, placeId)
                .orElseThrow(() -> new PlaceNotFoundException("해당 플랜에 장소가 없습니다."));
    }

}
