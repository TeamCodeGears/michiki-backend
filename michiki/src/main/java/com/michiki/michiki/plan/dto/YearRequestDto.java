package com.michiki.michiki.plan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class YearRequestDto {
    @NotNull
    private Integer year;

}
