package com.michiki.michiki.plan.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanMemberDto {
    private Long memberId;
    private String nickname;
    private String profileImage;
    private String color;

}
