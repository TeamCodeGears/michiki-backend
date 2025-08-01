package com.michiki.michiki.place.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
public class PlaceReorderRequestDto {

    @NotNull
    private LocalDate travelDate;

    @NotEmpty
    private List<PlaceOrder> places;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlaceOrder {
        @NotNull
        private Long placeId;
        @NotNull
        private Integer orderInDay;
    }
}
