package com.michiki.michiki.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 온라인 유저 상태 정보 DTO
public class MemberOnlineStatusDto {
    private Long memberId;
    private String nickname;
    private String profileImage;
    private boolean isOnline;
}