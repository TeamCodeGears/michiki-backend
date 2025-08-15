package com.michiki.michiki.plan.dto;


import lombok.Data;

@Data
public class MousePosition
{
    private Long memberId;
    private Long planId;
    private double x;
    private double y;
}
