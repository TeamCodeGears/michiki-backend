package com.michiki.michiki.place.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceUpdateRequestDto {
    @NotNull
    private Long placeId;
    private String description;
}
