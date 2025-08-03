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
import java.util.List;

@Getter
@Table(name = "PLANS")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@SequenceGenerator(name = "plans_seq_gen", sequenceName = "PLANS_SEQ", allocationSize = 1)
public class Plan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plans_seq_gen")
    private Long planId;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "SHARE_URI", nullable = false)
    private String shareURI;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Place> places;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberPlan> memberPlans;

    public void updateShareURI(String uri) {
        this.shareURI = uri;
    }

}