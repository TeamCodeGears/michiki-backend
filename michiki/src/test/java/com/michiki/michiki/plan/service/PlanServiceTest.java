package com.michiki.michiki.plan.service;

import com.michiki.michiki.place.repository.PlaceRepository;
import com.michiki.michiki.plan.dto.PlanDetailResponseDto;
import com.michiki.michiki.plan.entity.Plan;
import com.michiki.michiki.plan.repository.PlanRepository;
import com.michiki.michiki.plan.dto.PlanRequestDto;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {


    @Mock
    private PlanRepository planRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaceRepository placeRepository;


    @InjectMocks
    private PlanService planService;


    @Test
    void testCreatePlan() {
        // given
        Long memberId = 1L;
        PlanRequestDto dto = PlanRequestDto.builder()
                .title("제주도 여행")
                .startDate(java.time.LocalDate.of(2025, 9, 1))
                .endDate(java.time.LocalDate.of(2025, 9, 10))
                .build();

        Member mockMember = Member.builder()
                .memberId(memberId)
                .nickname("테스트유저")
                .email("test@example.com")
                .build();

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(mockMember));

        // when
        planService.createPlan(memberId, dto);

        // then
        verify(planRepository, times(1)).save(any());
    }



    @Test
    void 공유URI_회원_처음참여_자동가입_성공() {
        // given
        Plan plan = Plan.builder()
                .planId(1L)
                .title("제주도 여행")
                .startDate(LocalDate.of(2025, 8, 20))
                .endDate(LocalDate.of(2025, 8, 25))
                .shareURI("abc123")
                .memberPlans(new ArrayList<>()) // 처음이니까 비어있음
                .build();

        Member member = Member.builder()
                .memberId(100L)
                .email("test@example.com")
                .build();

        // Stub - 아래 두 값이 호출 시 동일한지 다시 확인!
        when(planRepository.findByShareURI("abc123")).thenReturn(Optional.of(plan));
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));
        when(placeRepository.findByPlanOrderByTravelDateAsc(plan)).thenReturn(Collections.emptyList());

        // PlanService 내부에서 "abc123"과 "test@example.com"으로 그대로 호출해야 함
        // when
        PlanDetailResponseDto result = planService.joinPlanByShareURI("abc123", "test@example.com");

        // then
        assertThat(result.getTitle()).isEqualTo("제주도 여행");
        assertThat(plan.getMemberPlans()).hasSize(1);
        assertThat(plan.getMemberPlans().get(0).getMember()).isEqualTo(member);

        verify(planRepository).save(plan); // 자동 저장 여부 확인
    }

}
