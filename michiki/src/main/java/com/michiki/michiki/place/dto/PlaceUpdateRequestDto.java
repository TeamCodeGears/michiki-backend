package com.michiki.michiki.place.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data

// 장소 수정시 사용되는 Dto
public class PlaceUpdateRequestDto {
    private String name;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String googlePlacedId;
}
