package com.michiki.michiki.pivot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberPlanId implements java.io.Serializable{
    private Long member;
    private Long host;
    private Long plan;
}
