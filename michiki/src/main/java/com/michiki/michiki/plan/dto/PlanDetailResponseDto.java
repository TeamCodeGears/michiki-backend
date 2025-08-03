package com.michiki.michiki.plan.dto;

import com.michiki.michiki.place.dto.PlaceResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PlanDetailResponseDto {

    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PlaceResponseDto> places;

    private String shareUri;

    // 추후 사용될 필드
    //private String backgroundColor;
   // private List<MemberResponseDto> activeMembers;
   // private List<MemberResponseDto> inactiveMembers;
   // private List<PlaceResponseDto> places;
}
