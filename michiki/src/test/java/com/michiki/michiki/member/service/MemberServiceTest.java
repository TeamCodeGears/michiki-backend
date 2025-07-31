package com.michiki.michiki.member.service;

import com.michiki.michiki.common.auth.service.RefreshTokenService;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.repository.MemberRepository;
import com.michiki.michiki.member.type.SocialType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void withdrawByEmail() {
        String email = "test@naver.com";
        Member mockMember = Member.builder()
                .memberId(12L)
                .email(email)
                .socialType(SocialType.GOOGLE)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));

        memberService.withdrawByEmail(email);

        InOrder inOrder = inOrder(memberRepository, refreshTokenService);

        inOrder.verify(refreshTokenService).deleteRefreshToken(email);
        inOrder.verify(memberRepository).delete(mockMember);
    }
}