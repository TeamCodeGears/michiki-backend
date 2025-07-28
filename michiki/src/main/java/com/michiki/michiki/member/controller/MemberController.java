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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@Tag(name = "회원 API", description = "회원 로그인 및 로그아웃 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleService googleService;
    private final RefreshTokenService refreshTokenService;

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

        // 사용자정보 얻기
        GoogleProfileDto googleProfileDto = googleService.getGoogleProfile(accessTokenDto.getAccessToken());

        // 회원가입이 되어 있지 않다면 회원가입
        Member originalMember = memberService.getMemberBySocialId(googleProfileDto.getSub());
        if (originalMember == null) {
            originalMember = memberService.createOauth(googleProfileDto.getSub(), googleProfileDto.getEmail(), SocialType.GOOGLE);
        }

        //회원 가입 되있는 회원이라면 토큰발급
        // 내부 JWT 토큰 발급 (액세스 토큰 + 리프레시 토큰)
        String accessToken = jwtTokenProvider.createAccessToken(originalMember.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(originalMember.getEmail());

        // 리프레시 토큰은 별도의 저장소에 저장해야 합니다 (DB 또는 Redis 등)
        refreshTokenService.saveRefreshToken(originalMember.getEmail(), refreshToken);

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getMemberId());
        loginInfo.put("accessToken", accessToken);
        loginInfo.put("refreshToken", refreshToken);


        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }


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
        String email = jwtTokenProvider.validateToken(accessToken).getSubject();

        // 리프레시 토큰 삭제
        refreshTokenService.deleteRefreshToken(email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃 성공");

        return ResponseEntity.ok(response);
    }
}
