package com.michiki.michiki.plan.dto;

import com.michiki.michiki.plan.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PlanResponseDto {
   private Long planId;
   private String title;
   private LocalDate startDate;
   private LocalDate endDate;

   public static PlanResponseDto fromEntity(Plan plan) {
      return PlanResponseDto.builder()
              .planId(plan.getPlanId())
              .title(plan.getTitle())
              .startDate(plan.getStartDate())
              .endDate(plan.getEndDate())
              .build();

   }
}
