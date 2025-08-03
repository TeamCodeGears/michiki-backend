package com.michiki.michiki.plan.controller;

import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.plan.dto.PlanResponseDto;
import com.michiki.michiki.plan.dto.YearRequestDto;
import com.michiki.michiki.plan.service.PlanService;
import com.michiki.michiki.plan.dto.MemberOnlineStatusDto;
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

    @GetMapping("/{planId}/members/online-members")
    public ResponseEntity<List<MemberOnlineStatusDto>> getOnlineMembers(
            @PathVariable Long planId){
        List<MemberOnlineStatusDto> onlineMembers = planService.getOnlineMembers(planId);
        return ResponseEntity.ok(onlineMembers);
    }
    // 공유 URI
    @PostMapping("/{planId}/share")
    public ResponseEntity<Map<String, String>> sharePlan(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetails userDetails){
        String uri = planService.generateShareUri(planId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("uri", uri));
    }

    @DeleteMapping("/{planId}/share")
    public ResponseEntity<Void> cancelShareUri(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetails userDetails){
        planService.cancelShareUri(planId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{planId}/share/status")
    public ResponseEntity<Map<String, Object>> getShareStatus(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetails userDetails){
        Map<String, Object> status = planService.getShareStatus(planId, userDetails.getUsername());
        return ResponseEntity.ok(status);
    }
}