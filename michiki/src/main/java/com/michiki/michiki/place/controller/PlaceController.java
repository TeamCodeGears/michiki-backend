package com.michiki.michiki.place.controller;

import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.place.dto.*;
import com.michiki.michiki.place.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "장소 API", description = "장소 생성, 삭제, 수정 등API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")


// 장소 관련 요청 처리하는 컨트롤러
public class PlaceController {

    private final PlaceService placeService;
    private final MemberService memberService;


    // 장소 등록 API
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
        placeService.addPlace(planId, getMemberId(userDetails), placeRequestDto);
        return ResponseEntity.ok(Map.of("message", "장소 등록 성공"));
    }

    // 장소 설명 수정 API
    @Operation(
            summary = "장소 수정",
            description = "planId 와 로그인한 멤버 정보를 바탕으로 특정 장소의 설명을 수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검사 실패 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 계획 또는 장소")
    })
    @PutMapping("/{planId}/places")
    public ResponseEntity<Map<String, String>> updatePlace(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PlaceUpdateRequestDto placeUpdateRequestDto
    ) {
        placeService.updatePlace(getMemberId(userDetails), planId, placeUpdateRequestDto);
        return ResponseEntity.ok(Map.of("message", "수정 성공"));
    }

    // 장소 삭제 API
    @Operation(
            summary = "장소 삭제",
            description = "planId 와 로그인한 멤버 정보를 바탕으로 해당 계획의 모든 장소를 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(responseCode = "204", description = "No Content (메시지 없이 완료)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 계획 또는 장소")
    })
    @DeleteMapping("/{planId}/places/{placeId}")
    public ResponseEntity<Map<String, String>> deletePlace(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long placeId
    ) {
        placeService.deletePlace(getMemberId(userDetails), planId, placeId);
        return ResponseEntity.ok(Map.of("message", "삭제 성공"));
    }

    // 장소 순서 재정렬  API
    @Operation(
            summary = "장소 순서 재정렬",
            description = "travelDate 별로 들어온 순서대로 orderInDay 를 업데이트하고, 재정렬된 장소 리스트를 반환합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlaceResponseDto.class,
                                    type = "array"))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 계획 또는 장소")
    })
    @PutMapping("/{planId}/places/reorder")
    public ResponseEntity<List<PlaceResponseDto>> reorderPlace(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PlaceReorderRequestDto placeReorderRequestDto
    ) {
        Long memberId = getMemberId(userDetails);
        List<PlaceResponseDto> reordered = placeService.reorderPlaces(memberId, planId, placeReorderRequestDto);
        return ResponseEntity.ok(reordered);
    }

    // 추천 장소 조회 API
    @Operation(
            summary = "추천 장소 목록 조회",
            description = "줌 레벨과 중심 좌표를 기준으로 추천 장소 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "추천 장소 목록 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlaceResponseDto.class, type = "array")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 계획")
    })

    @GetMapping("/{planId}/recommendations")
    public ResponseEntity<List<PlaceResponseDto>> getRecommendedPlace(
            @PathVariable Long planId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PlaceRecommendationRequestDto requestDto
    ) {
        Long memberId = getMemberId(userDetails);
        List<PlaceResponseDto> recommendations =
                placeService.recommendPlaces(memberId, planId, requestDto);
        return ResponseEntity.ok(recommendations);
    }

    // 유저 인증 정보에서 memberID 추출
    private Long getMemberId(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return memberService.findByMember(email).getMemberId();
    }
}


