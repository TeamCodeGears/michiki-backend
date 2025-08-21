package com.michiki.michiki.plan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequestDto {
    @NotBlank
    private String title;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
}
