package com.michiki.michiki.plan.controller;

import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.repository.MemberRepository;
import com.michiki.michiki.pivot.entity.MemberPlan;
import com.michiki.michiki.pivot.entity.repository.MemberPlanRepository;
import com.michiki.michiki.place.entity.Place;
import com.michiki.michiki.place.repository.PlaceRepository;
import com.michiki.michiki.plan.dto.PlanDetailResponseDto;
import com.michiki.michiki.plan.dto.PlanRequestDto;
import com.michiki.michiki.plan.dto.PlanResponseDto;
import com.michiki.michiki.plan.entity.Plan;
import com.michiki.michiki.plan.repository.PlanRepository;
import com.michiki.michiki.plan.service.PlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PlanControllerTest {
    @InjectMocks
    private PlanService planService;

    @Mock
    private MemberPlanRepository memberPlanRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private PlaceRepository placeRepository;

    private Member member;
    private Plan plan;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        member = Member.builder()
                .memberId(1L)
                .email("hi@test.com")
                .nickname("닉네임")
                .build();
        plan = Plan.builder()
                .planId(10L)
                .title("여행계획")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 9, 10))
                .memberPlans(new ArrayList<>())
                .build();
    }

    @Test
    void createPlan_success() {
        // given
        PlanRequestDto dto = PlanRequestDto.builder()
                .title("제주도")
                .startDate(LocalDate.of(2025, 10, 1))
                .endDate(LocalDate.of(2025, 10, 5))
                .build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        planService.createPlan(1L, dto);

        // then
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    void getPlansStartInYear() {
        List<Plan> plans = List.of(plan);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(planRepository.findAllByMemberIdAndStartDateInYear(eq(1L), any(), any()))
                .thenReturn(plans);

        List<PlanResponseDto> result = planService.getPlansStartInYear(1L, 2025);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("여행계획");
   }

    @Test
    void leavePlan_lastDeletes() {
        plan.getMemberPlans().add(new MemberPlan(member, plan, "#123456"));
        when(planRepository.findById(plan.getPlanId())).thenReturn(Optional.of(plan));

        String result = planService.leavePlan(1L, 10L);
        assertThat(result).contains("삭제");
        verify(planRepository).delete(plan);
    }

    @Test
    void changeColor_success() {
        MemberPlan memberPlan = new MemberPlan(member, plan, "#123456");
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(planRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(memberPlanRepository.findByMemberAndPlan(member, plan)).thenReturn(Optional.of(memberPlan));

        planService.changeColor(1L, 10L, "#fff000");
        assertThat(memberPlan.getColor()).isEqualTo("#fff000");
    }

    @Test
    void getPlanDetail_success() {
        List<MemberPlan> planMembers = List.of(new MemberPlan(member, plan, "#123456"));
        List<Place> places = List.of();

        plan.setMemberPlans(new ArrayList<>(planMembers));
        when(planRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(placeRepository.findByPlanOrderByTravelDateAsc(plan)).thenReturn(places);

        PlanDetailResponseDto detail = planService.getPlanDetail(10L);
        assertThat(detail.getPlanId()).isEqualTo(10L);
        assertThat(detail.getTitle()).isEqualTo(plan.getTitle());
    }

    @Test
    void getPlanByShareURI_success() {
        when(planRepository.findByShareURI("shareURI")).thenReturn(Optional.of(plan));
        when(placeRepository.findByPlanOrderByTravelDateAsc(plan)).thenReturn(Collections.emptyList());
        PlanDetailResponseDto result = planService.getPlanByShareURI("shareURI");
        assertThat(result.getPlanId()).isEqualTo(plan.getPlanId());
    }

    @Test
    void joinPlanByShareURI_join() {
        when(planRepository.findByShareURI("uri1")).thenReturn(Optional.of(plan));
        when(memberRepository.findByEmail("x@x.com")).thenReturn(Optional.of(member));
        when(placeRepository.findByPlanOrderByTravelDateAsc(plan)).thenReturn(Collections.emptyList());

        PlanDetailResponseDto result = planService.joinPlanByShareURI("uri1", "x@x.com");
        assertThat(plan.getMemberPlans()).hasSize(1);
        verify(planRepository).save(plan); // 자동저장 확인
    }

}