package com.michiki.michiki.plan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeColorRequestDto {
    @NotBlank
    private String Color;
}