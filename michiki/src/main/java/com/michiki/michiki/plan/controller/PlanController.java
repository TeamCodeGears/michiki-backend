package com.michiki.michiki.plan.controller;

import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.plan.dto.*;
import com.michiki.michiki.plan.service.NotificationService;
import com.michiki.michiki.plan.service.PlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

// 여행 계획 관련 요청을 처리하는 컨트롤러
public class PlanController {

    private final PlanService planService;
    private final MemberService memberService;
    private final NotificationService notificationService;


    // 연도별 여행 계획 목록 조회
    @GetMapping
    public ResponseEntity<List<PlanResponseDto>> getPlansByStartYear(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year) {
        Long memberId = getMemberId(userDetails);
        List<PlanResponseDto> plans = planService.getPlansStartInYear(memberId, year);
        if (plans.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(plans);
    }

    // 여행 계획 생성
    @PostMapping
    public ResponseEntity<Map<String, String>> createPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PlanRequestDto planRequestDto){

        planService.createPlan(getMemberId(userDetails), planRequestDto);
        return ResponseEntity.ok(Map.of("message", "계획 등록 성공"));
    }

    // 여행 계획 나가기
    @PostMapping("/{planId}")
    public ResponseEntity<Map<String, String>> leavePlan(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        String message = planService.leavePlan(memberId, planId);
        return ResponseEntity.ok(Map.of("message", "방" + message + " 성공"));
    }

    // 여행 계획 색상 변경
    @PostMapping("/{planId}/newColor")
    public ResponseEntity<Map<String, String>> changeColor(
            @PathVariable Long planId,
            @Valid @RequestBody ChangeColorRequestDto changeColorRequestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = getMemberId(userDetails);
        planService.changeColor(memberId, planId, changeColorRequestDto.getColor());
        return ResponseEntity.ok(Map.of("message", "변경 성공"));


    }
    // 현재 로그인한 사용자의 memberId 조회
    private Long getMemberId(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return memberService.findByMember(email).getMemberId();
    }

    // 여행 계획 상세 정보 조회
    @GetMapping("/{planId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PlanDetailResponseDto> getPlanDetail(
            @PathVariable Long planId) {
        PlanDetailResponseDto response = planService.getPlanDetail(planId);
        return ResponseEntity.ok(response);
    }

    //uri기반 계획 조회
    @GetMapping("/share/{shareURI}")
    public ResponseEntity<PlanDetailResponseDto> getPlanByShareURI(
            @PathVariable String shareURI,
            @AuthenticationPrincipal UserDetails userDetails) {

        //로그인 안함 -> 편집x 관전
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.ok(planService.getPlanByShareURI(shareURI));
        }
        // 로그인 -> 자동 참여
        return ResponseEntity.ok(planService.joinPlanByShareURI(shareURI, userDetails.getUsername()));

    }
    // 알림 페이지 확인 -> 알림 읽음 처리
    @PostMapping("/notifications/read")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> markNotificationsAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = memberService.findByMember(userDetails.getUsername()).getMemberId();
        // 전부 읽음 처리
        notificationService.markAllAsRead(memberId);
        return ResponseEntity.ok().build();
    }

    //uri기반 계획 조회
    @GetMapping("/share/{shareURI}")
    public ResponseEntity<PlanDetailResponseDto> getPlanByShareURI(
            @PathVariable String shareURI,
            @AuthenticationPrincipal UserDetails userDetails) {

        //로그인 안함 -> 편집x 관전
        if (userDetails == null || userDetails.getUsername() == null) {
            return ResponseEntity.ok(planService.getPlanByShareURI(shareURI));
        }
        // 로그인 -> 자동 참여
        return ResponseEntity.ok(planService.joinPlanByShareURI(shareURI, userDetails.getUsername()));
    }

    }
