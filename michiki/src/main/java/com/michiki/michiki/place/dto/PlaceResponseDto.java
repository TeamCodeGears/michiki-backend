package com.michiki.michiki.place.dto;

import com.michiki.michiki.place.entity.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PlaceResponseDto {
    private Long placeId;
    private Long memberId;
    private Long planId;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDate travelDate;
    private Integer orderInDay;

    public static PlaceResponseDto fromEntity(Place place) {
       return PlaceResponseDto.builder()
               .placeId(place.getPlaceId())
               .memberId(place.getMember().getMemberId())
               .planId(place.getPlan().getPlanId())
               .name(place.getName())
               .latitude(place.getLatitude())
               .longitude(place.getLongitude())
               .travelDate(place.getTravelDate())
               .orderInDay(place.getOrderInDay())
               .build();

    }
}
