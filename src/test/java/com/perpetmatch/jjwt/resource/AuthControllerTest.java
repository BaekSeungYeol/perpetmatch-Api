package com.perpetmatch.jjwt.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Domain.Member;
import com.perpetmatch.Member.MemberRepository;
import com.perpetmatch.Member.MemberService;
import com.perpetmatch.jjwt.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberService memberService;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    JwtTokenProvider tokenProvider;
    @Autowired
    AuthenticationManager authenticationManager;


    @BeforeEach
    void beforeEach() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .nickname("백승열입니다")
                .email("beck22222@naver.com")
                .password("12345678").build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("이메일로 로그인 하기")
    void login_with_email() throws Exception {

        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("beck22222@naver.com")
                .password("12345678")
                .build();

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("tokenType").value("Bearer"));
    }


    @Test
    @DisplayName("닉네임으로 로그인 하기")
    void login_with_nickname() throws Exception {

        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("백승열입니다")
                .password("12345678")
                .build();

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("tokenType").value("Bearer"));
    }

    @DisplayName("로그인 실패 - 가입되어 있지 않은 유저")
    @Test
    void login_fail() throws Exception {

        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("익명입니다")
                .password("12345678")
                .build();

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
//    @DisplayName("로그아웃")
//    @Test
}