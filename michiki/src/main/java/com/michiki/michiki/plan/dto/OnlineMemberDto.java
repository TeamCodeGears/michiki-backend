package com.michiki.michiki.plan.dto;

import lombok.Data;

@Data
public class OnlineMemberDto {
    private Long memberId;
    private Long planId;
    private String nickname;
    private String profileImage;

    public OnlineMemberDto(Long memberId, Long planId, String nickname, String profileImage) {
        this.memberId = memberId;
        this.planId = planId;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
