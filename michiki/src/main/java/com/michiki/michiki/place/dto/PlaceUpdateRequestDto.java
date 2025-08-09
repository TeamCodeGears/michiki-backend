package com.michiki.michiki.place.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

// 장소 수정시 사용되는 Dto
public class PlaceUpdateRequestDto {
    @NotNull
    private Long placeId;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String googlePlacedId;
}
