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

    @Column(name = "TITLE", length = 255, nullable = false)
    private String title;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "SHARE_URI", length = 255, nullable = false)
    private String shareURI;

    @Column(name = "SHARE_URI_EXPIRES_AT")
    private LocalDateTime shareUriExpiresAt;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Place> places;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberPlan> memberPlans;

    public LocalDateTime getShareUriExpiresAt() {
        return this.shareUriExpiresAt;
    }

    public void clearShareURI() {
        this.shareURI = null;
        this.shareUriExpiresAt = null;
    }

    public void updateShareURI(String token, LocalDateTime expiresAt) {
        this.shareURI = token;
        this.shareUriExpiresAt = expiresAt;
    }
}
