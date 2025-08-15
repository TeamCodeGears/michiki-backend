package com.michiki.michiki.plan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michiki.michiki.common.config.SecurityConfig;
import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.plan.dto.PlanDetailResponseDto;
import com.michiki.michiki.plan.service.PlanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = PlanController.class)
@Import(SecurityConfig.class) // ← 니가 만든 Security 설정 클래스가 있다면 추가
@AutoConfigureMockMvc(addFilters = false) // 보안 필터 무시

class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanService planService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService; // ← 추가


    @Test
    @DisplayName("비로그인 상태에서 공유 URI 조회 시 관람모드로 응답")
    void 공유URI_비회원조회_성공() throws Exception {
        // given
        PlanDetailResponseDto mockResponse = PlanDetailResponseDto.builder()
                .planId(1L)
                .title("테스트 계획")
                .build();

        Mockito.when(planService.getPlanByShareURI(anyString()))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/plans/share/sample-uri"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planId").value(1L))
                .andExpect(jsonPath("$.title").value("테스트 계획"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("로그인한 사용자가 공유 URI로 참여할 때 응답")
    void 공유URI_회원조회_참여됨() throws Exception {
        // given
        PlanDetailResponseDto mockResponse = PlanDetailResponseDto.builder()
                .planId(2L)
                .title("참여된 계획")
                .build();

        Mockito.when(planService.joinPlanByShareURI(anyString(), anyString()))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/plans/share/sample-uri"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planId").value(2L))
                .andExpect(jsonPath("$.title").value("참여된 계획"));
    }

}
