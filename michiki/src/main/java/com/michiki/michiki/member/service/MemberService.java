package com.michiki.michiki.member.service;

import com.michiki.michiki.member.MemberRepository;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.type.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public Member getMemberBySocialId(String socialId) {
        Member member = memberRepository.findBySocialId(socialId).orElse(null);
        return member;
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
}
