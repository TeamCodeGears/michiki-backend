package com.michiki.michiki.plan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// 색상 변경
public class ChangeColorRequestDto {
    @NotBlank
    private String Color;
}