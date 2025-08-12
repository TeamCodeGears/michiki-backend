package com.michiki.michiki.place.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.place.dto.PlaceReorderRequestDto;
import com.michiki.michiki.place.dto.PlaceRequestDto;
import com.michiki.michiki.place.dto.PlaceResponseDto;
import com.michiki.michiki.place.dto.PlaceUpdateRequestDto;
import com.michiki.michiki.place.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlaceController.class)
@AutoConfigureMockMvc
@Import(PlaceControllerTest.TestConfig.class)
class PlaceControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PlaceService placeService() {
            return Mockito.mock(PlaceService.class);
        }
        @Bean
        public MemberService memberService() {
            return Mockito.mock(MemberService.class);
        }
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired PlaceService placeService;
    @Autowired MemberService memberService;

    private final Long MEMBER_ID    = 42L;
    private final String MEMBER_EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        when(memberService.findByMember(MEMBER_EMAIL))
                .thenReturn(Member.builder()
                        .memberId(MEMBER_ID)
                        .email(MEMBER_EMAIL)
                        .build());
    }

    @Test
    void createPlace_Success() throws Exception {
        PlaceRequestDto dto = PlaceRequestDto.builder()
                .name("장소 A")
                .description("설명")
                .latitude(new BigDecimal("37.5"))
                .longitude(new BigDecimal("127.0"))
                .googlePlaceId("G123")
                .travelDate(LocalDate.of(2025,7,30))
                .orderInDay(1)
                .build();

        mockMvc.perform(post("/plans/{planId}/places", 1L)
                        .with(user(MEMBER_EMAIL))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("장소 등록 성공"));

        verify(placeService).addPlace(eq(1L), eq(MEMBER_ID), refEq(dto));
    }

    @Test
    void updatePlace_Success() throws Exception {
        PlaceUpdateRequestDto dto = new PlaceUpdateRequestDto();
        dto.setDescription("수정된 설명");

        mockMvc.perform(put("/plans/{planId}/places/{placeId}", 1L, 1L)
                        .with(user(MEMBER_EMAIL))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("수정 성공"));

        verify(placeService).updatePlace(MEMBER_ID, 1L, 1L, dto);
    }

    @Test
    void deletePlace_Success() throws Exception {
        mockMvc.perform(delete("/plans/{planId}/places/{placeId}", 1L, 10L)
                        .with(user(MEMBER_EMAIL))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("삭제 성공"));

        verify(placeService).deletePlace(MEMBER_ID, 1L, 10L);
    }

    @Test
    void reorderPlace_Success() throws Exception {
        // travelDate와 places 필드 모두 세팅
        PlaceReorderRequestDto reorderDto = new PlaceReorderRequestDto();
        reorderDto.setTravelDate(LocalDate.of(2025, 7, 30));
        reorderDto.setPlaces(List.of(
                new PlaceReorderRequestDto.PlaceOrder(11L, 1),
                new PlaceReorderRequestDto.PlaceOrder(10L, 2)
        ));

        List<PlaceResponseDto> responseList = List.of(
                PlaceResponseDto.builder()
                        .placeId(11L)
                        .name("장소2")
                        .orderInDay(1)
                        .build(),
                PlaceResponseDto.builder()
                        .placeId(10L)
                        .name("장소1")
                        .orderInDay(2)
                        .build()
        );

        when(placeService.reorderPlaces(MEMBER_ID, 1L, reorderDto)).thenReturn(responseList);

        mockMvc.perform(put("/plans/{planId}/places/reorder", 1L)
                        .with(user(MEMBER_EMAIL))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reorderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placeId").value(11L))
                .andExpect(jsonPath("$[0].name").value("장소2"))
                .andExpect(jsonPath("$[0].orderInDay").value(1))
                .andExpect(jsonPath("$[1].placeId").value(10L))
                .andExpect(jsonPath("$[1].name").value("장소1"))
                .andExpect(jsonPath("$[1].orderInDay").value(2));

        verify(placeService).reorderPlaces(MEMBER_ID, 1L, reorderDto);
    }

}