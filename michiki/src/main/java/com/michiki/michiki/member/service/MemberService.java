package com.michiki.michiki.member.service;

import com.michiki.michiki.common.auth.service.RefreshTokenService;
import com.michiki.michiki.common.exception.MemberNotFoundException;
import com.michiki.michiki.member.repository.MemberRepository;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.type.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service

// 회원 관련 비즈니스 로직을 담당하는 서비스 클래스
public class MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;

    // 소셜 ID 기준으로 회원 조회
    public Member getMemberBySocialId(String socialId) {
        return memberRepository.findBySocialId(socialId).orElse(null);
    }

    // 소셜 로그인 기반 회원 생성
    public Member createOauth(String socialId, String email, SocialType socialType) {
        Member member = Member.builder()
                .email(email)
                .socialType(socialType)
                .socialId(socialId)
                .build();
        memberRepository.save(member);
        return member;
    }

    // 회원 탈퇴 처리
    @Transactional
    public void withdrawByEmail(String email) {
        Member member = findByMember(email);
        refreshTokenService.deleteRefreshToken(email);
        memberRepository.delete(member);
    }

    // 이메일로 회원 조회
    public Member findByMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));
    }
}
