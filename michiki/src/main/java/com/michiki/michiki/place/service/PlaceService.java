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

    @Transactional
    public void addPlace(Long planId, Long memberId, PlaceRequestDto placeRequestDto) {
        Plan plan = getPlan(planId);
        Member member = getMember(memberId);

        memberValidate(memberId, plan);

        Place place = createPlace(placeRequestDto, plan, member);

        placeRepository.save(place);
    }

    @Transactional(readOnly = true)
    public void updatePlace(Long memberId, Long planId, PlaceUpdateRequestDto dto) {
        Plan plan = getPlan(planId);
        Member member = getMember(memberId);

        memberValidate(member.getMemberId(), plan);

        Place place = getPlace(plan, dto.getPlaceId());

        place.changeDescription(dto.getDescription());
    }

    @Transactional
    public void deletePlace(Long memberId, Long planId, Long placeId) {
        Member member = getMember(memberId);
        Plan plan = getPlan(planId);

        memberValidate(member.getMemberId(), plan);

        Place place = getPlace(plan, placeId);

        placeRepository.delete(place);
    }

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

        return updated.stream()
                .sorted(Comparator.comparing(Place::getOrderInDay))
                .map(PlaceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlaceResponseDto> recommendPlaces(Long memberId, Long planId, PlaceRecommendationRequestDto dto) {
        Plan plan = getPlan(planId);
        memberValidate(memberId, plan);

        // 1. 추천 후보 장소를 가져온다 (예: 중심 좌표 + zoomLevel 기반 필터링)
        List<Place> candidatePlaces = fetchCandidatePlaces(dto.getCenterLatitude(), dto.getCenterLongitude(), dto.getZoomLevel());

        // 2. 엔티티를 DTO로 매핑해서 반환
        return candidatePlaces.stream()
                .map(PlaceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // TODO: 실제 로직은 DB 쿼리나 외부 API 호출로 대체해야 한다.
// 예시용으로 중심 좌표에서 일정 거리 이내인 더미 필터링을 해보는 형태로 만들면 이렇게 확장 가능하다.
    private List<Place> fetchCandidatePlaces(Double centerLat, Double centerLng, Float zoomLevel) {
        // placeholder: 실제라면 zoomLevel을 기반으로 반경을 계산해서 DB에서 조건에 맞는 장소를 조회.
        // 예: zoomLevel이 클수록 더 좁은 반경 -> radius 계산 (여기서는 단순화)
        double radiusKm = zoomLevelToRadiusKm(zoomLevel);

        // 예시: 모든 장소를 가져와서 거리 계산 후 필터 (실제라면 JPA 쿼리로 변경)
        List<Place> allPlaces = placeRepository.findAll(); // 필요한 경우 스코프 제한 (예: 인기 장소, 특정 지역 등)
        return allPlaces.stream()
                .filter(p -> {
                    double distance = haversine(centerLat, centerLng,
                            p.getLatitude().doubleValue(), p.getLongitude().doubleValue());
                    return distance <= radiusKm;
                })
                .toList();
    }

    // zoomLevel을 반경(km)으로 변환하는 간단한 예시
    private double zoomLevelToRadiusKm(Float zoomLevel) {
        // 이 함수는 비례적으로 조정할 수 있고, 실제 요구에 맞게 튜닝 필요
        // 예: zoomLevel 1 -> 100km, 10 -> 10km, 15 -> 2km 식으로
        return Math.max(1.0, 100.0 / zoomLevel);
    }

    // Haversine formula: 두 좌표 사이 거리 (km)
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private static void memberValidate(Long memberId, Plan plan) {
        boolean participates = plan.getMemberPlans().stream()
                .anyMatch(mp -> mp.getMember().getMemberId().equals(memberId));
        if (!participates) {
            throw new NotParticipatingMemberException("해당 계획에 참여중이 아닙니다.");
        }
    }

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

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 사용자입니다."));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("존재하지 않는 계획입니다."));
    }

    private Place getPlace(Plan plan, Long placeId) {
        return placeRepository.findByPlanAndPlaceId(plan, placeId)
                .orElseThrow(() -> new PlaceNotFoundException("해당 플랜에 장소가 없습니다."));
    }

}