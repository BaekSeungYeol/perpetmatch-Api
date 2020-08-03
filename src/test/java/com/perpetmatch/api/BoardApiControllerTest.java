package com.perpetmatch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Board.Gender;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.PetAge.PetAgeRepository;
import com.perpetmatch.apiDto.Board.BoardRequest;
import com.perpetmatch.common.RestDocsConfiguration;
import com.perpetmatch.jjwt.resource.LoginRequest;
import com.perpetmatch.jjwt.resource.SignUpRequest;
import com.perpetmatch.pet.PetRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class BoardApiControllerTest {



    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PetRepository petRepository;
    @Autowired
    UserService userService;
    @Autowired
    PetAgeRepository petAgeRepository;

    String token = null;

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

        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("beck22222@naver.com")
                .password("12345678")
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()).andReturn();


        TokenTest findToken = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TokenTest.class);
        token = findToken.getTokenType() + " " + findToken.getAccessToken();
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("게시글 등록 테스트 - 성공 ")
    public void createBoard() throws Exception {
        BoardRequest boardRequest = BoardRequest.builder()
                .title("버려진 포메 보호하고 있습니다")
                .credit(100000)
                .zone("Seoul,서울특별시")
                .gender(Gender.MALE)
                .year(1)
                .month(11)
                .petTitle("치와와")
                .checkUp("DataURL")
                .lineAgeImage("DataURL")
                .neuteredImage("DataURL")
                .description("이 친구는 어떠 어떠하며 어떠 어떠한 특성을 가지고 있고 어떠 어떠한 습관을 가지고 있어요.")
                .boardImage1("DataURL")
                .boardImage2("DataURL")
                .boardImage3("DataURL")
                .build();

        mockMvc.perform(post("/api/board/one")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("게시글이 등록 되었습니다."))
                .andExpect(jsonPath("data.id").exists())
                .andExpect(jsonPath("data.title").exists())

                .andDo(document("create-board",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("credit").type(JsonFieldType.NUMBER).description("껌 (보증금)"),
                                fieldWithPath("zone").type(JsonFieldType.STRING).description("지역"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("성별"),
                                fieldWithPath("year").type(JsonFieldType.NUMBER).description("나이(년)"),
                                fieldWithPath("month").type(JsonFieldType.NUMBER).description("나이(개월)"),
                                fieldWithPath("petTitle").type(JsonFieldType.STRING).description("품종"),
                                fieldWithPath("checkUp").type(JsonFieldType.STRING).description("건강검진 이미지"),
                                fieldWithPath("lineAgeImage").type(JsonFieldType.STRING).description("혈통서 이미지"),
                                fieldWithPath("neuteredImage").type(JsonFieldType.STRING).description("중성화 이미지"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("소개"),
                                fieldWithPath("boardImage1").type(JsonFieldType.STRING).description("강아지 이미지1"),
                                fieldWithPath("boardImage2").type(JsonFieldType.STRING).description("강아지 이미지2"),
                                fieldWithPath("boardImage3").type(JsonFieldType.STRING).description("강아지 이미지3")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("해당 유저의 프로필 조회입니다."),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("id"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목")
                        )));

    }
}