package com.michiki.michiki.place.service;


import com.michiki.michiki.common.exception.MemberNotFoundException;
import com.michiki.michiki.common.exception.NotParticipatingMemberException;
import com.michiki.michiki.common.exception.PlanNotFoundException;
import com.michiki.michiki.member.MemberRepository;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.place.dto.PlaceRequestDto;
import com.michiki.michiki.place.entity.Place;
import com.michiki.michiki.place.repository.PlaceRepository;
import com.michiki.michiki.plan.entity.Plan;
import com.michiki.michiki.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static void memberValidate(Long memberId, Plan plan) {
        boolean participates = plan.getMemberPlans().stream()
                .anyMatch(mp -> mp.getMember().getMemberId().equals(memberId));
        if(!participates) {
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
}
