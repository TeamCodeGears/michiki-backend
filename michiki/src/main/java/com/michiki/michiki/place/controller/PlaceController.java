package com.michiki.michiki.place.controller;

import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.place.dto.PlaceRequestDto;
import com.michiki.michiki.place.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "계획 API", description = "계획 생성, 삭제, 수정 등PI")
@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlaceController {

    private final PlaceService placeService;
    private final MemberService memberService;

    @Operation(summary = "새로운 장소 등록", description = "planId 와 로그인한 멤버 정보를 바탕으로 장소를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장소 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 유효성 검사 실패 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 계획 또는 멤버")
    })
    @PostMapping("{planId}/places")
    public ResponseEntity<Map<String, String>> createPlace(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PlaceRequestDto placeRequestDto
            ) {
        String email = userDetails.getUsername();
        Member member = memberService.findByMember(email);
        placeService.addPlace(planId, member.getMemberId(), placeRequestDto);
        return ResponseEntity.ok(Map.of("message", "장소 등록 성공"));
    }
}
