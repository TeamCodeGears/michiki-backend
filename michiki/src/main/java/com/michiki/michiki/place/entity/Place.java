package com.michiki.michiki.place.entity;

import com.michiki.michiki.common.BaseEntity;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.place.dto.PlaceUpdateRequestDto;
import com.michiki.michiki.plan.entity.Plan;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@SequenceGenerator(
        name = "place_seq_gen",
        sequenceName = "PLACES_SEQ",
        allocationSize = 1
)
@Table(name = "PLACES")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Place extends BaseEntity {

    // 장소 ID
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "place_seq_gen")
    private Long placeId;

    // 장소 등록한 유저
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 장소가 속한 여행 계획
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    // 장소 이름
    @Column(name = "NAME")
    private String name;

    // 장소 설명
    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    // 위도
    @Column(name = "LATITUDE", precision = 9, scale = 6, nullable = false)
    private BigDecimal latitude;

    // 경도
    @Column(name = "LONGITUDE", precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;

    // 구글 지도 장소 ID
    @Column(name = "GOOGLE_PLACE_ID")
    private String googlePlaceId;

    // 장소를 방문하는 날짜
    @Column(name = "TRAVEL_DATE", nullable = false)
    private LocalDate travelDate;

    // 하루 안에 순서
    @Column(name = "ORDER_IN_DAY", nullable = false)
    private Integer orderInDay;

    // 일별 순서 변경 메서드
    public void changeOrderInDay(@NotNull Integer orderInDay) {
        this.orderInDay = orderInDay;
    }

    public void changePlan(PlaceUpdateRequestDto dto) {
        this.name = dto.getName();
        this.description = dto.getDescription();
    }
}
