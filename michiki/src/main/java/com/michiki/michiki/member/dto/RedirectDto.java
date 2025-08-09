package com.michiki.michiki.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

// OAuth 인증 후 프론트엔드에서 전달받은 인가 코드를 담는 Dto
public class RedirectDto {
    private String code;
}
