package com.michiki.michiki.place.dto;

import lombok.Data;

@Data

// 장소 수정시 사용되는 Dto
public class PlaceUpdateRequestDto {
    private String name;
    private String description;
}
