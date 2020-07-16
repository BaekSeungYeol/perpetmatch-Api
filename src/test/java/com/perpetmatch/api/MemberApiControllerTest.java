package com.perpetmatch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Domain.Member;
import com.perpetmatch.Member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberApiControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 - 입력값 정상")
    public void createmember() throws Exception {

         Member member = Member.builder()
                .nickname("야호랑이")
                .email("beck33333@naver.com")
                .password("123456789")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(member)))
                .andDo(print())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("User registered successfully"));

        Member savedMember = memberRepository.findByEmail("beck33333@naver.com");
        assertNotNull(savedMember);

        assertTrue(memberRepository.existsByEmail("beck33333@naver.com"));
        assertNotNull(savedMember.getEmailCheckToken());
    }


    @Test
    @DisplayName("회원가입 - 입력값 오류")
    public void createmember_bad_request_Test() throws Exception {
        Member member = Member.builder()
                .nickname("백승")
                .password("125")
                .email("beck33")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("회원가입 - 중복 처리")
    public void createmember_bad_request_Double() throws Exception {
        Member member = Member.builder()
                .nickname("야호랑이")
                .password("123456789")
                .email("beck33333@naver.com")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(member)));

        Member member2 = Member.builder()
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
                .andExpect(jsonPath("message").value("Username is already taken!"));;

    }
    @Test
    @DisplayName("회원 가입 - 빈 입력값")
    public void createmember_bad_request_Test_Empty() throws Exception {
        Member member = Member.builder()
                .nickname("")
                .password("")
                .email("")
                .build();

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