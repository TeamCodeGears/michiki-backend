package com.michiki.michiki.pivot.entity;

import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.plan.entity.Plan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Table(name = "members_plans_pivot")
@IdClass(MemberPlanId.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MemberPlan {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAN_ID", nullable = false)
    private Plan plan;

    @Column(name = "color", nullable = false, length = 16)
    private String color;
}
