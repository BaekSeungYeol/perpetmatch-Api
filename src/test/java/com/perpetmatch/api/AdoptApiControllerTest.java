package com.perpetmatch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.jjwt.resource.*;
import com.perpetmatch.modules.Board.BoardRepository;
import com.perpetmatch.modules.Board.Gender;
import com.perpetmatch.api.dto.Board.AdoptMatchDto;
import com.perpetmatch.api.dto.Board.BoardPostRequest;
import com.perpetmatch.common.RestDocsConfiguration;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Transactional
@AutoConfigureMockMvc
class AdoptApiControllerTest {


    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    AuthController authController;
    @Autowired
    BoardApiController boardApiController;

    private String token;

    @BeforeEach
    void beforeEach() throws Exception {
        signUp();
        getToken();
    }

    private void getToken() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("beck22222@naver.com")
                .password("@!test1234")
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()).andReturn();


        TokenTest findToken = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TokenTest.class);
        token = findToken.getTokenType() + " " + findToken.getAccessToken();
    }

    private void signUp() {
        SignUpRequest request = SignUpRequest.builder()
                .nickname("백승열입니다")
                .email("beck22222@naver.com")
                .password("@!test1234").build();

        authController.registerMember(request);
    }

    @AfterEach
    void afterEach() {
        boardRepository.deleteAll();
    }

    @Test
    @DisplayName("입양하기 페이지 프로필 기반 검색 테스트")
    void AdoptSearch_profile() throws Exception {
        //given
        Long id = getBoardId();
        AdoptMatchDto dto = createAdoptMatchDto();

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/boards/profile/search")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(ApiResponseCode.OK.toString()))
                .andExpect(jsonPath("message").value("요청이 성공하였습니다."))
                .andExpect(jsonPath("data.content[0].id").exists())
                .andExpect(jsonPath("data.content[0].title").exists())
                .andExpect(jsonPath("data.content[0].credit").exists())
                .andExpect(jsonPath("data.content[0].year").exists())
                .andExpect(jsonPath("data.content[0].month").exists())
                .andExpect(jsonPath("data.content[0].tags").exists())
                .andExpect(jsonPath("data.content[0].boardImage1").exists())
                .andExpect(jsonPath("data.content[0].createdAt").exists())
                .andExpect(jsonPath("data.content[0].closed").exists())
                .andDo(document("searchProfile-board",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("code").type(JsonFieldType.STRING).description(ApiResponseCode.OK),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("해당 유저의 게시글입니다."),
                                fieldWithPath("data.content[0].id").type(JsonFieldType.NUMBER).description("ID"),
                                fieldWithPath("data.content[0].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("data.content[0].credit").type(JsonFieldType.NUMBER).description("껌 (보증금)"),
                                fieldWithPath("data.content[0].year").type(JsonFieldType.NUMBER).description("나이(년)"),
                                fieldWithPath("data.content[0].month").type(JsonFieldType.NUMBER).description("나이(개월)"),
                                fieldWithPath("data.content[0].tags").type(JsonFieldType.ARRAY).description("태그들"),
                                fieldWithPath("data.content[0].boardImage1").type(JsonFieldType.STRING).description("강아지 이미지1"),
                                fieldWithPath("data.content[0].createdAt").type(JsonFieldType.STRING).description("생성 날짜"),
                                fieldWithPath("data.content[0].closed").type(JsonFieldType.BOOLEAN).description("게시글이 닫혔는지 여부"),
                                fieldWithPath("data.content[0].hasCheckUp").type(JsonFieldType.BOOLEAN).description("건강검진 여부"),
                                fieldWithPath("data.content[0].hasLineAge").type(JsonFieldType.BOOLEAN).description("혈통서 여부")
                                )));
    }

    private AdoptMatchDto createAdoptMatchDto() {
        ArrayList<String> zones = new ArrayList<>();
        List<String> petTitles = new ArrayList<>();
        List<String> petAges = new ArrayList<>();
        boolean wantCheckUp = true;
        boolean wantLineAge = true;
        boolean wantNeutered = true;
        int credit = 10000;
        return new AdoptMatchDto(zones,petTitles,petAges,wantCheckUp,wantLineAge,wantNeutered,credit);
    }

    @Test
    @DisplayName("입양하기 페이지 제목 검색 테스트 ")
    void AdoptSearch_keyword() throws Exception {
        //given
        Long id = getBoardId();

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/boards/search?keyword=포메")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(ApiResponseCode.OK.toString()))
                .andExpect(jsonPath("message").value("요청이 성공하였습니다."))
                .andExpect(jsonPath("data.content[0].id").exists())
                .andExpect(jsonPath("data.content[0].title").exists())
                .andExpect(jsonPath("data.content[0].credit").exists())
                .andExpect(jsonPath("data.content[0].year").exists())
                .andExpect(jsonPath("data.content[0].month").exists())
                .andExpect(jsonPath("data.content[0].tags").exists())
                .andExpect(jsonPath("data.content[0].boardImage1").exists())
                .andExpect(jsonPath("data.content[0].createdAt").exists())
                .andDo(document("search-board",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("code").type(JsonFieldType.STRING).description(ApiResponseCode.OK),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청이 성공하였습니다."),
                                fieldWithPath("data.content[0].id").type(JsonFieldType.NUMBER).description("ID"),
                                fieldWithPath("data.content[0].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("data.content[0].credit").type(JsonFieldType.NUMBER).description("껌 (보증금)"),
                                fieldWithPath("data.content[0].year").type(JsonFieldType.NUMBER).description("나이(년)"),
                                fieldWithPath("data.content[0].month").type(JsonFieldType.NUMBER).description("나이(개월)"),
                                fieldWithPath("data.content[0].tags").type(JsonFieldType.ARRAY).description("태그들"),
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
        //given
        BoardPostRequest boardRequest = BoardPostRequest.builder()
                .title("버려진 포메 보호하고 있습니다")
                .credit(100000)
                .zone("서울특별시")
                .gender(Gender.MALE)
                .year(1)
                .month(11)
                .petTitle("치와와")
                .checkUpImage("https://S3image.org/")
                .lineAgeImage("https://S3image.org/")
                .neuteredImage("https://S3image.org/")
                .hasNeutered(true)
                .description("이 친구는 어떠 어떠하며 어떠 어떠한 특성을 가지고 있고 어떠 어떠한 습관을 가지고 있어요.")
                .boardImage1("https://S3image.org/")
                .boardImage2("https://S3image.org/")
                .boardImage3("https://S3image.org/")
                .build();

        //when
        this.mockMvc.perform(post("/api/boards")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isOk());

        Long id = boardRepository.findByTitle("버려진 포메 보호하고 있습니다").getId();
        return id;
    }
}