package com.perpetmatch.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Domain.Member;
import com.perpetmatch.Member.MemberRepository;
import com.perpetmatch.apiDto.ProfileRequest;
import com.perpetmatch.jjwt.resource.LoginRequest;
import com.perpetmatch.jjwt.resource.SignUpRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.sun.activation.registries.LogSupport.log;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class ProfileApiControllerTest {


    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RestTemplate restTemplate;


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


    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile_success() throws Exception {

//        LoginRequest loginRequest = LoginRequest.builder()
//                .usernameOrEmail("백승열입니다")
//                .password("12345678")
//                .build();
//
//        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("http://localhost:8080/api/auth/signin", loginRequest, String.class);
//        String body = stringResponseEntity.getBody();
//        Map<String,String> map = objectMapper.readValue(body, new TypeReference<Map<String,String>>(){});
//        StringBuilder accessToken = new StringBuilder();
//        accessToken.append("Bearer ");
//        accessToken.append(map.get("accessToken"));


        ProfileRequest profileRequest = ProfileRequest.builder()
                .houseType("아파트")
                .occupation("약사")
                .experience(false)
                .liveAlone(false)
                .howManyPets(2)
                .expectedFeeForMonth(100000)
                .location("서울")
                .profileImage("fdklfkdslfksdflks")
                .phoneNumber("010-3926-6280")
                .build();

        mockMvc.perform(post("/api/profiles")
            //    .header("Authorization", accessToken.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("Profile updated successfully"));

        Member member = memberRepository.findByNickname("백승열").get();
        assertEquals(member.getHouseType(), "아파트");
    }
    @DisplayName("프로필 수정하기 - 입력값 실패")
    @Test
    void updateProfile_failed() throws Exception {

    }
}