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
    void 공유URI_비회원관람_성공() {
        // given
        Plan plan = Plan.builder()
                .planId(1L)
                .title("제주도 여행")
                .startDate(LocalDate.of(2025, 8, 20))
                .endDate(LocalDate.of(2025, 8, 25))
                .shareURI("abc123")
                .build();

        when(planRepository.findByShareURI("abc123")).thenReturn(Optional.of(plan));
        when(placeRepository.findByPlanOrderByTravelDateAsc(plan)).thenReturn(Collections.emptyList());

        // when
        PlanDetailResponseDto result = planService.getPlanByShareURI("abc123");

        // then
        assertThat(result.getTitle()).isEqualTo("제주도 여행");
        assertThat(result.getPlaces()).isEmpty();
        verify(planRepository).findByShareURI("abc123");
    }

    @Test
    void 공유URI_없으면_예외발생() {
        // given
        when(planRepository.findByShareURI("invalid")).thenReturn(Optional.empty());

        // when & then
        assertThrows(PlanNotFoundException.class, () -> planService.getPlanByShareURI("invalid"));
    }
}
