package com.michiki.michiki.pivot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

// Memberplan entity의 복합 기본 키를 정의하는 식별자 클래스
public class MemberPlanId implements java.io.Serializable{
    private Long member;
    private Long plan;
}
