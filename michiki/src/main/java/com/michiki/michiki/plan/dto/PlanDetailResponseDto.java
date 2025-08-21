package com.michiki.michiki.plan.dto;

import com.michiki.michiki.place.dto.PlaceResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
// 계획 상세 정보 응답 DTO
public class PlanDetailResponseDto {
    private Long planId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<PlanMemberDto> members;
    private List<PlaceResponseDto> places;
}
