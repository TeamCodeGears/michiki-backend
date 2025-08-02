package com.michiki.michiki.plan.controller;

import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.plan.dto.ChangeColorRequestDto;
import com.michiki.michiki.plan.dto.PlanResponseDto;
import com.michiki.michiki.plan.dto.YearRequestDto;
import com.michiki.michiki.plan.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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


    @PostMapping("/{planId}")
    public ResponseEntity<Map<String, String>> leavePlan(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        String message = planService.leavePlan(memberId, planId);
        return ResponseEntity.ok(Map.of("message", "방" + message + " 성공"));
    }



    @PostMapping("/{planId}/newColor")
    public ResponseEntity<Map<String, String>> changeColor(
            @PathVariable Long planId,
            @Valid @RequestBody ChangeColorRequestDto changeColorRequestDto,
            @AuthenticationPrincipal UserDetails userDetails
            ){
        Long memberId = getMemberId(userDetails);
        planService.changeColor(memberId, planId, changeColorRequestDto.getColor());
        return ResponseEntity.ok(Map.of("message", "변경 성공"));


    }

    private Long getMemberId(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return memberService.findByMember(email).getMemberId();
    }
}
