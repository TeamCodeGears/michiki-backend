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

// 회원과 계획 간의 관계를 나타내는 중간 entity
public class MemberPlan {

    // 참여 유저
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    // 참여 중 계획
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAN_ID", nullable = false)
    private Plan plan;

    // 해당 유저가 설정한 계획 색상
    @Column(name = "color", nullable = false, length = 16)
    private String color;

    // 색상 변경 로직
    public void changeColor (String newColor) {
        this.color = newColor;
    }
}
