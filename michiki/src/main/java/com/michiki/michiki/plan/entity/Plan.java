package com.michiki.michiki.plan.entity;

import com.michiki.michiki.common.BaseEntity;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.place.entity.Place;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Length;

import java.util.List;
import java.time.LocalDate;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HOST_ID", nullable = false)
    private Member host;

    @Column(name = "TITLE", length = 255, nullable = false)
    private String title;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "SHARE_URI", length = 255, nullable = false)
    private String shareURI;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Place> places;

    public void updateShareURI(String uri) {
        this.shareURI = uri;
    }
}