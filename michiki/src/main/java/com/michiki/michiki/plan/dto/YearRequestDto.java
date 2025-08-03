package com.michiki.michiki.plan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

//연도 기반 여행 계획 목록을 조회 요청 Dto
public class YearRequestDto {
    @NotNull
    private Integer year;
}
