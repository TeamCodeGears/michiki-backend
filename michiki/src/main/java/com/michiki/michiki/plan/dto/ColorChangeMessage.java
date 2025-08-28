package com.michiki.michiki.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColorChangeMessage {
    private Long planId;
    private Long memberId;
    private String color;
    private String nickname;
}
