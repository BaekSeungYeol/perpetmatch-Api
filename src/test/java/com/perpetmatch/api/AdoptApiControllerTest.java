package com.perpetmatch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Board.BoardRepository;
import com.perpetmatch.Board.Gender;
import com.perpetmatch.api.dto.Board.BoardPostRequest;
import com.perpetmatch.common.RestDocsConfiguration;
import com.perpetmatch.jjwt.resource.LoginRequest;
import com.perpetmatch.jjwt.resource.SignUpRequest;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@AutoConfigureMockMvc
class AdoptApiControllerTest {


    @Autowired
    MockMvc  mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    BoardRepository boardRepository;

    private String token;

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
    }

    @Test
    @DisplayName("입양하기 페이지 제목 검색 테스트 ")
    public void AdoptSearch_keyword() throws Exception {
        //given
        Long id = getBoardId();

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/boards/v1/search?keyword=포메")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("입양 게시판 검색입니다."))
                .andExpect(jsonPath("data.content[0].id").exists())
                .andExpect(jsonPath("data.content[0].title").exists())
                .andExpect(jsonPath("data.content[0].credit").exists())
                .andExpect(jsonPath("data.content[0].zone").exists())
                .andExpect(jsonPath("data.content[0].year").exists())
                .andExpect(jsonPath("data.content[0].month").exists())
                .andExpect(jsonPath("data.content[0].petTitle").exists())
                .andExpect(jsonPath("data.content[0].petAge").exists())
                .andExpect(jsonPath("data.content[0].hasCheckUp").exists())
                .andExpect(jsonPath("data.content[0].hasLineAgeImage").exists())
                .andExpect(jsonPath("data.content[0].hasNeutered").exists())
                .andExpect(jsonPath("data.content[0].description").exists())
                .andExpect(jsonPath("data.content[0].boardImage1").exists())
                .andExpect(jsonPath("data.content[0].createdAt").exists())
                .andDo(document("search-board",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("해당 유저의 게시글입니다."),
                                fieldWithPath("data.content[0].id").type(JsonFieldType.NUMBER).description("ID"),
                                fieldWithPath("data.content[0].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("data.content[0].credit").type(JsonFieldType.NUMBER).description("껌 (보증금)"),
                                fieldWithPath("data.content[0].zone").type(JsonFieldType.STRING).description("지역"),
                                fieldWithPath("data.content[0].year").type(JsonFieldType.NUMBER).description("나이(년)"),
                                fieldWithPath("data.content[0].month").type(JsonFieldType.NUMBER).description("나이(개월)"),
                                fieldWithPath("data.content[0].petTitle").type(JsonFieldType.STRING).description("품종"),
                                fieldWithPath("data.content[0].petAge").type(JsonFieldType.STRING).description("나이 범위"),
                                fieldWithPath("data.content[0].hasCheckUp").type(JsonFieldType.BOOLEAN).description("건강검진 이미지 여부"),
                                fieldWithPath("data.content[0].hasLineAgeImage").type(JsonFieldType.BOOLEAN).description("혈통서 이미지 여부"),
                                fieldWithPath("data.content[0].hasNeutered").type(JsonFieldType.BOOLEAN).description("중성화 이미지 여부"),
                                fieldWithPath("data.content[0].description").type(JsonFieldType.STRING).description("소개"),
                                fieldWithPath("data.content[0].boardImage1").type(JsonFieldType.STRING).description("강아지 이미지1"),
                                fieldWithPath("data.content[0].createdAt").type(JsonFieldType.STRING).description("생성 날짜"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("검색 조건과 부합하는 게시글 갯수"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 갯수"),
                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("처음 페이지 인지 여부"),
                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 인지 여부"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 넘버 -1, +1을 통해 다음 페이지로 가는 링크만들 수 있다")
                        )));
    }

    private Long getBoardId() throws Exception {
        BoardPostRequest boardRequest = BoardPostRequest.builder()
                .title("버려진 포메 보호하고 있습니다")
                .credit(100000)
                .zone("서울특별시")
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

        mockMvc.perform(post("/api/boards")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isOk());

        Long id = boardRepository.findByTitle("버려진 포메 보호하고 있습니다").getId();
        return id;
    }
}