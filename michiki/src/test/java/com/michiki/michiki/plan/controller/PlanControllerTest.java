package com.michiki.michiki.plan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.plan.dto.PlanDetailResponseDto;
import com.michiki.michiki.plan.service.NotificationService;
import com.michiki.michiki.plan.service.PlanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlanController.class)
@Import(PlanControllerTest.TestSecurityConfig.class)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanService planService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("알림 읽음 처리 API - 성공")
    @WithMockUser(username = "test@example.com")
    void markNotificationsAsRead_success() throws Exception {
        Member fakeMember = Member.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("nickname")
                .build();

        Mockito.when(memberService.findByMember("test@example.com"))
                .thenReturn(fakeMember);

        Mockito.doNothing().when(notificationService).markAllAsRead(1L);

        mockMvc.perform(post("/plans/notifications/read")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(notificationService, Mockito.times(1))
                .markAllAsRead(1L);
    }

    @Test
    @DisplayName("공유 URI 조회 - 비로그인(관전자)")
    void getPlanByShareURI_asGuest() throws Exception {
        // given
        PlanDetailResponseDto dummyResponse = PlanDetailResponseDto.builder()
                .planId(100L)
                .title("테스트 계획")
                .build();

        Mockito.when(planService.getPlanByShareURI("abc123"))
                .thenReturn(dummyResponse);

        // when & then
        mockMvc.perform(get("/plans/share/{shareURI}", "abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planId").value(100L))
                .andExpect(jsonPath("$.title").value("테스트 계획"));

        Mockito.verify(planService, Mockito.times(1)).getPlanByShareURI("abc123");
        Mockito.verify(planService, Mockito.never()).joinPlanByShareURI(anyString(), anyString());
    }

    @Test
    @DisplayName("공유 URI 조회 - 로그인(자동 참여)")
    @WithMockUser(username = "test@example.com")
    void getPlanByShareURI_asAuthenticatedUser() throws Exception {
        // given
        PlanDetailResponseDto dummyResponse = PlanDetailResponseDto.builder()
                .planId(200L)
                .title("참여된 계획")
                .build();

        Mockito.when(planService.joinPlanByShareURI("xyz789", "test@example.com"))
                .thenReturn(dummyResponse);

        // when & then
        mockMvc.perform(get("/plans/share/{shareURI}", "xyz789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planId").value(200L))
                .andExpect(jsonPath("$.title").value("참여된 계획"));

        Mockito.verify(planService, Mockito.times(1))
                .joinPlanByShareURI("xyz789", "test@example.com");

        Mockito.verify(planService, Mockito.never())
                .getPlanByShareURI(anyString());
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }
}
