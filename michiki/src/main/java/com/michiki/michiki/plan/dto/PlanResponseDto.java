package com.michiki.michiki.plan.dto;

import com.michiki.michiki.plan.entity.Plan;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

// 여행 계획 목록 조회시 응답으로 사용하는 Dto
public class PlanResponseDto {
   private Long planId;
   private String title;
   private LocalDate startDate;
   private LocalDate endDate;

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
