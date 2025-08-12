package com.michiki.michiki.place.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data

// 장소 수정시 사용되는 Dto
public class PlaceUpdateRequestDto {
    private String description;
}
