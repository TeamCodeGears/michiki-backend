package com.michiki.michiki.plan.dto;

import com.michiki.michiki.plan.entity.Plan;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
   private String dDay;

   // plan Entity -> Dto 변환 메서드
   public static PlanResponseDto fromEntity(Plan plan) {
       return PlanResponseDto.builder()
               .planId(plan.getPlanId())
               .title(plan.getTitle())
               .startDate(plan.getStartDate())
               .endDate(plan.getEndDate())
               .dDay(calculateDday(plan.getStartDate(), plan.getEndDate()))
               .build();
   }
      // D-day 계산
   private static String calculateDday(LocalDate startDate, LocalDate endDate){
       LocalDate today= LocalDate.now();

       if(today.isBefore(startDate)) {
           long days = ChronoUnit.DAYS.between(today, startDate);
           return "D-" + days;
       }else if (!today.isAfter(endDate)) {
           return "여행 중";
       }else {
           return "여행 종료";
       }
   }
}
