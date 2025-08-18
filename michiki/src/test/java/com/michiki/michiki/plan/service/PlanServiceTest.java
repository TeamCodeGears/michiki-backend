package com.michiki.michiki.plan.service;

import com.michiki.michiki.common.exception.PlanNotFoundException;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.repository.MemberRepository;
import com.michiki.michiki.pivot.entity.MemberPlan;
import com.michiki.michiki.plan.dto.PlanDetailResponseDto;
import com.michiki.michiki.plan.entity.Plan;
import com.michiki.michiki.plan.repository.PlanRepository;
import com.michiki.michiki.place.entity.Place;
import com.michiki.michiki.place.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.ArrayList;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlanService planService;

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

        when(planRepository.findByShareURI("abc123")).thenReturn(Optional.of(plan));
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));
        when(placeRepository.findByPlanOrderByTravelDateAsc(plan)).thenReturn(Collections.emptyList());

        // when
        PlanDetailResponseDto result = planService.joinPlanByShareURI("abc123", "test@example.com");

        // then
        assertThat(result.getTitle()).isEqualTo("제주도 여행");
        assertThat(plan.getMemberPlans()).hasSize(1);
        assertThat(plan.getMemberPlans().get(0).getMember()).isEqualTo(member);

        verify(planRepository).save(plan); // 자동 저장 여부 확인
    }

}
