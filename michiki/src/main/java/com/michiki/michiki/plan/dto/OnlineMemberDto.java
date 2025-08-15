package com.michiki.michiki.plan.dto;

import lombok.Data;

@Data
public class OnlineMemberDto {
    private Long memberId;
    private Long planId;
    private String nickName;
    private String profileImage;
}
