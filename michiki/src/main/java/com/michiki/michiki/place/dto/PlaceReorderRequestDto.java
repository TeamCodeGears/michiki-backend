package com.michiki.michiki.place.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data

// 장소 순서 재정렬 요청시 사용하는 Dto
public class PlaceReorderRequestDto {

    @NotNull
    private LocalDate travelDate;

    @NotEmpty
    private List<PlaceOrder> places;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor

    // 개별 장소 ID와 정렬 순서 담는 내부 정적 클래스
    public static class PlaceOrder {
        @NotNull
        private Long placeId;

        @NotNull
        private Integer orderInDay;
    }
}
