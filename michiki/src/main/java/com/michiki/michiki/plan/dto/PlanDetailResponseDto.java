package com.michiki.michiki.plan.dto;

import com.michiki.michiki.place.dto.PlaceResponseDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

// 계획 상세 정보 응답 DTO
public class PlanDetailResponseDto {
    private Long planId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String shareUri;
    private List<PlaceResponseDto> places;
}
