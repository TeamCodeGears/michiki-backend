package com.michiki.michiki.plan.dto;

import com.michiki.michiki.plan.entity.Plan;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PlanResponseDto {
   private Long planId;
   private String title;
   private BigDecimal latitude;
   private BigDecimal longtitude;
   private String googlePlacedId;
   private LocalDate startDate;
   private LocalDate endDate;
   private LocalDate travelDate;
   private int orderInDay;

   public static PlanResponseDto fromEntity(Plan plan) {
      return PlanResponseDto.builder()
              .planId(plan.getPlanId())
              .title(plan.getTitle())
              .startDate(plan.getStartDate())
              .endDate(plan.getEndDate())
              .build();

   }
}
