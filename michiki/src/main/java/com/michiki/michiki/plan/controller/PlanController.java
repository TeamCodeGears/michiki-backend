package com.michiki.michiki.plan.controller;

import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.plan.dto.PlanResponseDto;
import com.michiki.michiki.plan.dto.YearRequestDto;
import com.michiki.michiki.plan.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {

    private final PlanService planService;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<PlanResponseDto>> getPlansByStartYear(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody YearRequestDto yearRequestDto) {
        String email = userDetails.getUsername();
        Long memberId = memberService.findByMember(email).getMemberId();
        List<PlanResponseDto> plans = planService.getPlansStartInYear(memberId, yearRequestDto.getYear());
        return ResponseEntity.ok(plans);
    }
}
