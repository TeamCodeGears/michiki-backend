package com.michiki.michiki.plan.service;

import com.michiki.michiki.plan.repository.PlanRepository;
import com.michiki.michiki.plan.dto.PlanRequestDto;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PlanService planService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
}
