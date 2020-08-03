package com.perpetmatch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Domain.User;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.common.RestDocsConfiguration;
import com.perpetmatch.jjwt.resource.LoginRequest;
import com.perpetmatch.jjwt.resource.SignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class pMemberApiControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 - 입력값 정상")
    public void signin() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .nickname("백승열입니다")
                .email("beck22222@naver.com")
                .password("12345678").build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("beck22222@naver.com")
                .password("12345678")
                .build();

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andDo(document("sign-in",
                requestHeaders(
                        headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON")
                ),
                requestFields(
                        fieldWithPath("usernameOrEmail").type(JsonFieldType.STRING).description("유저 닉네임 혹은 이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                ),
                responseFields(
                        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("JWT 토큰"),
                        fieldWithPath("tokenType").type(JsonFieldType.STRING).description("Bearer 헤더")
                )

        ));
    }

    @Test
    @DisplayName("회원가입 - 입력값 정상")
    public void createmember() throws Exception {

        SignUpRequest member = SignUpRequest.builder()
                .nickname("야호랑이")
                .email("beck33333@naver.com")
                .password("123456789")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(member)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("회원가입이 성공적으로 완료되었습니다."))
                .andDo(document("sign-up",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("결과성공여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지")
                        )

                ));

        User savedMember = userRepository.findByEmail("beck33333@naver.com");
        assertNotNull(savedMember);

        assertTrue(userRepository.existsByEmail("beck33333@naver.com"));
        assertNotNull(savedMember.getEmailCheckToken());
    }


    @Test
    @DisplayName("회원가입 - 입력값 오류")
    public void createmember_bad_request_Test() throws Exception {
        User member = new User();
        member.setNickname("백승");
        member.setPassword("125");
        member.setEmail("beck33");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("회원가입 - 중복 처리")
    public void createmember_bad_request_Double() throws Exception {
        SignUpRequest member = SignUpRequest.builder()
                .nickname("야호랑이")
                .password("123456789")
                .email("beck33333@naver.com")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(member)));

        SignUpRequest member2 = SignUpRequest.builder()
                .nickname("야호랑이")
                .password("123456789")
                .email("beck33333@naver.com")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("해당 이름이 이미 존재합니다."));

    }
    @Test
    @DisplayName("회원 가입 - 빈 입력값")
    public void createmember_bad_request_Test_Empty() throws Exception {
        User member = new User();
        member.setNickname("");
        member.setPassword("");
        member.setEmail("");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isBadRequest());

    }

//    @DisplayName("인증 메일 확인 - 입력값 오류")
//    @Test
//    void checkEmailToken_with_wrong_input() throws Exception {
//        mockMvc.perform(get("/check-email-token")
//                .param("token", "sdfkldsfklds")
//                .param("email", "email@email.com"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @DisplayName("인증 메일 확인 - 입력값 정상")
//    @Test
//    void checkEmailToken_with_right_input() throws Exception {
//        Member member = Member.builder()
//                .email("test@email.com")
//                .password("12345678")
//                .nickname("seungyeol")
//                .build();
//
//        Member newMember = memberRepository.save(member);
//        newMember.generateEmailCheckToken();
//
//        mockMvc.perform(get("/check-email-token")
//                .param("token", newMember.getEmailCheckToken())
//                .param("email", newMember.getEmail()))
//                .andExpect(status().isOk());
//    }

}