package com.michiki.michiki.plan.dto;

import com.michiki.michiki.plan.entity.Plan;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

// 여행 계획 목록 조회시 응답으로 사용하는 Dto
public class PlanResponseDto {
   private Long planId;
   private String title;
   private BigDecimal latitude;
   private BigDecimal longitude;
   private String googlePlacedId;
   private LocalDate startDate;
   private LocalDate endDate;
   private LocalDate travelDate;
   private int orderInDay;

   // plan Entity -> Dto 변환 메서드
   public static PlanResponseDto fromEntity(Plan plan) {
      return PlanResponseDto.builder()
              .planId(plan.getPlanId())
              .title(plan.getTitle())
              .startDate(plan.getStartDate())
              .endDate(plan.getEndDate())
              .build();

   }
}
