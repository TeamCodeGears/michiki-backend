package com.michiki.michiki.plan.dto;

import lombok.Data;

@Data
public class ChatMessage
{
    private Long memberId;
    private Long planId;
    private String message;
}
