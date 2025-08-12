package com.michiki.michiki.place.service;

import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.repository.MemberRepository;
import com.michiki.michiki.pivot.entity.MemberPlan;
import com.michiki.michiki.place.dto.PlaceReorderRequestDto;
import com.michiki.michiki.place.dto.PlaceRequestDto;
import com.michiki.michiki.place.dto.PlaceResponseDto;
import com.michiki.michiki.place.dto.PlaceUpdateRequestDto;
import com.michiki.michiki.place.entity.Place;
import com.michiki.michiki.place.repository.PlaceRepository;
import com.michiki.michiki.plan.entity.Plan;
import com.michiki.michiki.plan.repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {
    @Mock
    PlaceRepository placeRepository;

    @Mock
    PlanRepository planRepository;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    PlaceService placeService;

    private final Long PLAN_ID = 100L;
    private final Long MEMBER_ID = 200L;

    private Member member;
    private Plan plan;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .memberId(MEMBER_ID)
                .email("test@naver.com")
                .build();

        plan = mock(Plan.class);
        MemberPlan mp = mock(MemberPlan.class);
        when(mp.getMember()).thenReturn(member);
        when(plan.getMemberPlans()).thenReturn(List.of(mp));

        lenient().when(memberRepository.findById(MEMBER_ID))
                .thenReturn(Optional.of(member));
        lenient().when(planRepository.findById(PLAN_ID))
                .thenReturn(Optional.of(plan));
    }

    @Test
    void addPlace_Success() {
        PlaceRequestDto placeRequestDto = new PlaceRequestDto();
        placeRequestDto.setName("장소 A");
        placeRequestDto.setDescription("설명");
        placeRequestDto.setLatitude(new BigDecimal("37.5"));
        placeRequestDto.setLongitude(new BigDecimal("127.0"));
        placeRequestDto.setGooglePlaceId("googlePlaceId");
        placeRequestDto.setTravelDate(LocalDate.of(2025, 7, 30));
        placeRequestDto.setOrderInDay(1);

        placeService.addPlace(PLAN_ID, MEMBER_ID, placeRequestDto);

        ArgumentCaptor<Place> cap = ArgumentCaptor.forClass(Place.class);
        verify(placeRepository).save(cap.capture());

        Place place = cap.getValue();

        assertThat(place.getPlan()).isEqualTo(plan);
        assertThat(place.getMember()).isEqualTo(member);
        assertThat(place.getName()).isEqualTo("장소 A");

        assertThat(place.getLatitude()).isEqualByComparingTo(new BigDecimal("37.5"));
        assertThat(place.getLongitude()).isEqualByComparingTo(new BigDecimal("127.0"));
    }

    @Test
    void updatePlace_Success() {
        Long PLACE_ID = 300L;
        Place place = spy(Place.builder().placeId(PLACE_ID).build());
        when(placeRepository.findByPlanAndPlaceId(plan, PLACE_ID)).thenReturn(Optional.of(place));

        PlaceUpdateRequestDto dto = new PlaceUpdateRequestDto();
        dto.setDescription("새 설명");

        placeService.updatePlace(MEMBER_ID, PLAN_ID, PLACE_ID, dto);

        verify(place).changePlan(dto);
    }

    @Test
    void deletePlace_Success() {
        Long PLACE_ID = 300L;
        Place place = new Place();
        when(placeRepository.findByPlanAndPlaceId(plan, PLACE_ID)).thenReturn(Optional.of(place));

        placeService.deletePlace(MEMBER_ID, PLAN_ID, PLACE_ID);

        verify(placeRepository).delete(place);
    }

    @Test
    void reorderPlaces_Success() {
        LocalDate travelDate = LocalDate.of(2025, 7, 30);

        Place p1 = spy(Place.builder()
                .placeId(1L)
                .plan(plan)
                .member(member)
                .travelDate(travelDate)
                .build());
        Place p2 = spy(Place.builder()
                .placeId(2L)
                .plan(plan)
                .member(member)
                .travelDate(travelDate)
                .build());

        when(placeRepository.findByPlanAndTravelDate(plan, travelDate))
                .thenReturn(List.of(p1, p2));

        PlaceReorderRequestDto dto = new PlaceReorderRequestDto();
        dto.setTravelDate(travelDate);
        dto.setPlaces(List.of(
                new PlaceReorderRequestDto.PlaceOrder(2L, 1),
                new PlaceReorderRequestDto.PlaceOrder(1L, 2)
        ));

        List<PlaceResponseDto> result = placeService.reorderPlaces(MEMBER_ID, PLAN_ID, dto);

        assertThat(result).extracting("placeId").containsExactly(2L, 1L);

        verify(p2).changeOrderInDay(1);
        verify(p1).changeOrderInDay(2);
    }
}