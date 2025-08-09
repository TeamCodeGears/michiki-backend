package com.michiki.michiki.member.controller;


import com.michiki.michiki.common.auth.*;
import com.michiki.michiki.common.auth.dto.AccessTokenDto;
import com.michiki.michiki.common.auth.dto.GoogleProfileDto;
import com.michiki.michiki.common.auth.service.GoogleService;
import com.michiki.michiki.common.auth.service.RefreshTokenService;
import com.michiki.michiki.member.dto.RedirectDto;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.member.type.SocialType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@Tag(name = "회원 API", description = "회원 로그인 및 로그아웃 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/member")

// 회원 인증 및 계정 관련 기능을 제공하는 컨트롤러
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleService googleService;
    private final RefreshTokenService refreshTokenService;

    // 구글 OAtuth 로그인 처리
    @Operation(summary = "구글 로그인",
            description = "구글 OAuth 인증코드를 사용하여 로그인 처리 및 JWT 액세스/리프레시 토큰 발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "로그인 실패 또는 인증 실패")
    })

    @PostMapping("/google/login")
    public ResponseEntity<?> googleLogin(@RequestBody RedirectDto redirectDto) {
        // accessToken 발급
        AccessTokenDto accessTokenDto = googleService.getAccessToken(redirectDto.getCode());

        // 유저 정보 얻기
        GoogleProfileDto googleProfileDto = googleService.getGoogleProfile(accessTokenDto.getAccessToken());

        // nickname fallback (구글에서 name 없으면 email의 앞부분이나 timestamp 사용)
        String nickname = googleProfileDto.getName();
        if (nickname == null || nickname.isBlank()) {
            if(googleProfileDto.getEmail() != null && googleProfileDto.getEmail().contains("@")) {
                nickname = googleProfileDto.getEmail().split("@")[0];
            } else {
                nickname = "user_" + System.currentTimeMillis();
            }
        }

        // 회원가입이 되어 있지 않다면 회원가입
        Member originalMember = memberService.getMemberBySocialId(googleProfileDto.getSub());
        if (originalMember == null) {
            originalMember = memberService.createOauth(googleProfileDto.getSub(), googleProfileDto.getEmail(), SocialType.GOOGLE, nickname, googleProfileDto.getPicture());
        }

        //회원 가입 되있는 유저라면 토큰발급
        // 내부 JWT 토큰 발급 (액세스 토큰 + 리프레시 토큰)
        String accessToken = jwtTokenProvider.createAccessToken(originalMember.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(originalMember.getEmail());

        // 리프레시 토큰은 별도의 저장소에 저장해야 합니다 (DB 또는 Redis 등)
        refreshTokenService.saveRefreshToken(originalMember.getEmail(), refreshToken);

        // 로그인 응답 반환
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getMemberId());
        loginInfo.put("accessToken", accessToken);
        loginInfo.put("refreshToken", refreshToken);

        log.info("accessToken: " + accessToken);
        log.info("refreshToken: " + refreshToken);


        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    // 로그아웃 처리
    @Operation(summary = "로그아웃",
            description = "JWT 액세스 토큰을 사용해 로그아웃 처리 및 저장된 리프레시 토큰 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "권한 인증 실패")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization) {
        // Bearer 토큰에서 실제 토큰 값만 추출
        String accessToken = authorization.substring(7);

        // 유저 이메일 추출
        String email = jwtTokenProvider.validateToken(accessToken).getSubject();

        // 리프레시 토큰 삭제
        refreshTokenService.deleteRefreshToken(email);

        // 응답 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃 성공");

        return ResponseEntity.ok(response);
    }

    // 회원 탈퇴 처리
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 회원 탈퇴를 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않음")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        memberService.withdrawByEmail(email);
        return ResponseEntity.ok(Map.of("message", "회원 탈퇴 성공"));

    }
}
