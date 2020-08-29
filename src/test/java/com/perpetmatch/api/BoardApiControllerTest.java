package com.perpetmatch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Board.BoardRepository;
import com.perpetmatch.Board.Gender;
import com.perpetmatch.Domain.Board;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.PetAge.PetAgeRepository;
import com.perpetmatch.api.dto.Board.BoardPostRequest;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    BoardRepository boardRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PetRepository petRepository;
    @Autowired
    UserService userService;
    @Autowired
    PetAgeRepository petAgeRepository;

    private Long id;
    private String token = null;

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
    @DisplayName("게시글 단일 조회 테스트 - 성공")
    public void getBoard() throws Exception {
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

        id = boardRepository.findByTitle("버려진 포메 보호하고 있습니다").getId();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/boards/{id}", id)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("해당 유저의 게시글입니다."))
                .andExpect(jsonPath("data.id").exists())
                .andExpect(jsonPath("data.title").exists())
                .andExpect(jsonPath("data.manager").exists())
                .andExpect(jsonPath("data.credit").exists())
                .andExpect(jsonPath("data.zone").exists())
                .andExpect(jsonPath("data.gender").exists())
                .andExpect(jsonPath("data.year").exists())
                .andExpect(jsonPath("data.month").exists())
                .andExpect(jsonPath("data.petTitle").exists())
                .andExpect(jsonPath("data.petAge").exists())
                .andExpect(jsonPath("data.checkUp").exists())
                .andExpect(jsonPath("data.lineAgeImage").exists())
                .andExpect(jsonPath("data.neuteredImage").exists())
                .andExpect(jsonPath("data.description").exists())
                .andExpect(jsonPath("data.boardImage1").exists())
                .andExpect(jsonPath("data.boardImage2").exists())
                .andExpect(jsonPath("data.boardImage3").exists())
                .andDo(document("show-board",
                        pathParameters(
                                parameterWithName("id").description("아이디")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("해당 유저의 게시글입니다."),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("ID"),
                                fieldWithPath("data.manager").type(JsonFieldType.STRING).description("작성자(관리자)"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("data.credit").type(JsonFieldType.NUMBER).description("껌 (보증금)"),
                                fieldWithPath("data.zone.province").type(JsonFieldType.STRING).description("지역"),
                                fieldWithPath("data.gender").type(JsonFieldType.STRING).description("성별"),
                                fieldWithPath("data.year").type(JsonFieldType.NUMBER).description("나이(년)"),
                                fieldWithPath("data.month").type(JsonFieldType.NUMBER).description("나이(개월)"),
                                fieldWithPath("data.petTitle.title").type(JsonFieldType.STRING).description("품종"),
                                fieldWithPath("data.petAge.petRange").type(JsonFieldType.STRING).description("나이 범위"),
                                fieldWithPath("data.checkUp").type(JsonFieldType.STRING).description("건강검진 이미지"),
                                fieldWithPath("data.lineAgeImage").type(JsonFieldType.STRING).description("혈통서 이미지"),
                                fieldWithPath("data.neuteredImage").type(JsonFieldType.STRING).description("중성화 이미지"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("소개"),
                                fieldWithPath("data.boardImage1").type(JsonFieldType.STRING).description("강아지 이미지1"),
                                fieldWithPath("data.boardImage2").type(JsonFieldType.STRING).description("강아지 이미지2"),
                                fieldWithPath("data.boardImage3").type(JsonFieldType.STRING).description("강아지 이미지3"),
                                fieldWithPath("data.zone.id").type(JsonFieldType.NUMBER).description("zoneID"),
                                fieldWithPath("data.petTitle.id").type(JsonFieldType.NUMBER).description("petTItleID"),
                                fieldWithPath("data.petAge.id").type(JsonFieldType.NUMBER).description("petAgeID")
                        )));

    }

    @Test
    @DisplayName("게시글 등록 테스트 - 성공 ")
    public void createBoard() throws Exception {
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

    @Test
    @DisplayName("페이지 기능 정상 동작")
    public void slicing() {
        boardRepository.save(new Board());
        boardRepository.save(new Board());
        boardRepository.save(new Board());
        boardRepository.save(new Board());
        boardRepository.save(new Board());
        boardRepository.save(new Board());
        boardRepository.save(new Board());

        //when
        PageRequest pageRequest = PageRequest.of(0,6);
        Slice<Board> allBoards = boardRepository.findAllBoards(pageRequest);

        //then
        List<Board> content = allBoards.getContent();
        assertEquals(content.size(), 6);
        assertEquals(allBoards.getNumber(), 0);
        assertEquals(allBoards.getNumberOfElements(), 6);

    }

    @Test
    @DisplayName("입양하기 페이지 게시글 다건 조회 성공")
    public void getAllBoards_Adoption_page() throws Exception {

        //when
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
                .content(objectMapper.writeValueAsString(boardRequest)));

        //then
        mockMvc.perform(get("/api/boards")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("게시글 다건 조회입니다."))
                .andExpect(jsonPath("data.content").exists())
                .andDo(document("get-boards",
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("게시글 다건 조회입니다."),
                                fieldWithPath("data.content[0].id").type(JsonFieldType.NUMBER).description("id"),
                                fieldWithPath("data.content[0].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("data.content[0].credit").type(JsonFieldType.NUMBER).description("포인트"),
                                fieldWithPath("data.content[0].gender").type(JsonFieldType.STRING).description("성별"),
                                fieldWithPath("data.content[0].zone.province").type(JsonFieldType.STRING).description("지역"),
                                fieldWithPath("data.content[0].petTitle.title").type(JsonFieldType.STRING).description("품종"),
                                fieldWithPath("data.content[0].petAge.petRange").type(JsonFieldType.STRING).description("나이 범위"),
                                fieldWithPath("data.content[0].year").type(JsonFieldType.NUMBER).description("나이 (년)"),
                                fieldWithPath("data.content[0].month").type(JsonFieldType.NUMBER).description("나이 (월)"),
                                fieldWithPath("data.content[0].hasCheckup").type(JsonFieldType.BOOLEAN).description("건강검진증 여부"),
                                fieldWithPath("data.content[0].hasLineAgeImage").type(JsonFieldType.BOOLEAN).description("혈통서 여부"),
                                fieldWithPath("data.content[0].neutered").type(JsonFieldType.BOOLEAN).description("중성화 여부"),
                                fieldWithPath("data.content[0].publishedDateTime").type(JsonFieldType.STRING).description("작성일")
                        )));
    }
}