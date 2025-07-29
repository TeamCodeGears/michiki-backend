package com.michiki.michiki.member.service;

import com.michiki.michiki.common.auth.service.RefreshTokenService;
import com.michiki.michiki.common.exception.MemberNotFoundException;
import com.michiki.michiki.member.MemberRepository;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.type.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;

    public Member getMemberBySocialId(String socialId) {
        return memberRepository.findBySocialId(socialId).orElse(null);
    }

    public Member createOauth(String socialId, String email, SocialType socialType) {
        Member member = Member.builder()
                .email(email)
                .socialType(socialType)
                .socialId(socialId)
                .build();
        memberRepository.save(member);
        return member;
    }

    @Transactional
    public void withdrawByEmail(String email) {
        Member member = findByMember(email);
        refreshTokenService.deleteRefreshToken(email);
        memberRepository.delete(member);
    }

    public Member findByMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));
    }
}
