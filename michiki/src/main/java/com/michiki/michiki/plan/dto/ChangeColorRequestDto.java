package com.michiki.michiki.plan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

// 색상 변경
public class ChangeColorRequestDto {
    @NotBlank
    private String Color;
}