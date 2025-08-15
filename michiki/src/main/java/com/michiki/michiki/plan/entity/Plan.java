package com.michiki.michiki.plan.entity;

import com.michiki.michiki.common.BaseEntity;
import com.michiki.michiki.pivot.entity.MemberPlan;
import com.michiki.michiki.place.entity.Place;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Table(name = "PLANS")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@SequenceGenerator(name = "plans_seq_gen", sequenceName = "PLANS_SEQ", allocationSize = 1)

// 여행 게획을 나타내는 Entity
public class Plan extends BaseEntity {

    // 여행 계획 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plans_seq_gen")
    private Long planId;

    // 계획 제목
    @Column(name = "TITLE", nullable = false)
    private String title;

    // 여행 시작일
    @Column(name = "START_DATE")
    private LocalDate startDate;

    // 여행 종료일
    @Column(name = "END_DATE")
    private LocalDate endDate;

    // 공유 URI (토큰 기반)
    @Column(name = "SHARE_URI")
    private String shareURI;

    // 계획에 포함된 장소 목록
    @Builder.Default
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Place> places = new ArrayList<>();

    // 참여중인 유저 정보
    @Builder.Default
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberPlan> memberPlans = new ArrayList<>();

}
