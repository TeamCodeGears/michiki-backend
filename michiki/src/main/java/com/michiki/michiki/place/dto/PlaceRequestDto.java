package com.michiki.michiki.place.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 새로운 장소 등록 요청시 사용하는 Dto
public class PlaceRequestDto {
    @NotBlank
    private String name;
    private String description;

    @NotNull
    private BigDecimal latitude;

    @NotNull
    private BigDecimal longitude;
    private String googlePlaceId;

    @NotNull
    private LocalDate travelDate;

    @NotNull
    private Integer orderInDay;
}
