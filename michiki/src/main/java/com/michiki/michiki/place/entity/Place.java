package com.michiki.michiki.place.entity;

import com.michiki.michiki.common.BaseEntity;
import com.michiki.michiki.member.entity.Member;
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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "place_seq_gen")
    private Long placeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "LATITUDE", precision = 9, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;

    @Column(name = "GOOGLE_PLACE_ID")
    private String googlePlaceId;

    @Column(name = "TRAVEL_DATE", nullable = false)
    private LocalDate travelDate;

    @Column(name = "ORDER_IN_DAY", nullable = false)
    private Integer orderInDay;

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeTravelDate(@NotNull LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public void changeOrderInDay(@NotNull Integer orderInDay) {
        this.orderInDay = orderInDay;
    }
}
