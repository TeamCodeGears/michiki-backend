package com.michiki.michiki.place.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PlaceRecommendationRequestDto {
    @NotNull
    private Double centerLatitude;

    @NotNull
    private Double centerLongitude;

    @NotNull
    private Float zoomLevel;
}