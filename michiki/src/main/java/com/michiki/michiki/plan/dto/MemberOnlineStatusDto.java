package com.michiki.michiki.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberOnlineStatusDto {
    private Long memberId;
    private String nickname;
    private String profileImage;
    private boolean isOnline;
}
