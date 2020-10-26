package com.perpetmatch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Board.BoardRepository;
import com.perpetmatch.Board.Gender;
import com.perpetmatch.Domain.Board;
import com.perpetmatch.Domain.User;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.PetAge.PetAgeRepository;
import com.perpetmatch.api.dto.Board.BoardPostRequest;
import com.perpetmatch.api.dto.Board.NameDto;
import com.perpetmatch.api.dto.User.UserCredit;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        Long id = getBoardId();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/boards/{id}", this.id)
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
                .andExpect(jsonPath("data.checkUpImage").exists())
                .andExpect(jsonPath("data.lineAgeImage").exists())
                .andExpect(jsonPath("data.description").exists())
                .andExpect(jsonPath("data.boardImage1").exists())
                .andExpect(jsonPath("data.boardImage2").exists())
                .andExpect(jsonPath("data.boardImage3").exists())
                .andExpect(jsonPath("data.closed").exists())
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
                                fieldWithPath("data.zone").type(JsonFieldType.STRING).description("지역"),
                                fieldWithPath("data.gender").type(JsonFieldType.STRING).description("성별"),
                                fieldWithPath("data.year").type(JsonFieldType.NUMBER).description("나이(년)"),
                                fieldWithPath("data.month").type(JsonFieldType.NUMBER).description("나이(개월)"),
                                fieldWithPath("data.petTitle").type(JsonFieldType.STRING).description("품종"),
                                fieldWithPath("data.petAge").type(JsonFieldType.STRING).description("나이 범위"),
                                fieldWithPath("data.checkUpImage").type(JsonFieldType.STRING).description("건강검진 이미지"),
                                fieldWithPath("data.lineAgeImage").type(JsonFieldType.STRING).description("혈통서 이미지"),
                                fieldWithPath("data.hasNeutered").type(JsonFieldType.BOOLEAN).description("중성화 여부"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("소개"),
                                fieldWithPath("data.boardImage1").type(JsonFieldType.STRING).description("강아지 이미지1"),
                                fieldWithPath("data.boardImage2").type(JsonFieldType.STRING).description("강아지 이미지2"),
                                fieldWithPath("data.boardImage3").type(JsonFieldType.STRING).description("강아지 이미지3"),
                                fieldWithPath("data.closed").type(JsonFieldType.BOOLEAN).description("게시글이 수락되었는지 여부")
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
                .checkUpImage("DataURL")
                .lineAgeImage("DataURL")
                .neuteredImage("DataURL")
                .hasNeutered(true)
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
        return id;
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
                .checkUpImage("DataURL")
                .lineAgeImage("DataURL")
                .neuteredImage("DataURL")
                .hasNeutered(true)
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
                                fieldWithPath("checkUpImage").type(JsonFieldType.STRING).description("건강검진 이미지"),
                                fieldWithPath("lineAgeImage").type(JsonFieldType.STRING).description("혈통서 이미지"),
                                fieldWithPath("neuteredImage").type(JsonFieldType.STRING).description("중성화 이미지"),
                                fieldWithPath("hasNeutered").type(JsonFieldType.BOOLEAN).description("중성화 여부"),
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

//    @Test
//    @DisplayName("게시글 수정 테스트 - 성공 ")
//    public void updateBoard() throws Exception {
//        BoardPostRequest boardRequest = BoardPostRequest.builder()
//                .title("버려진 포메 보호하고 있습니다")
//                .credit(100000)
//                .zone("서울특별시")
//                .gender(Gender.MALE)
//                .year(1)
//                .month(11)
//                .petTitle("치와와")
//                .checkUpImage("DataURL")
//                .lineAgeImage("DataURL")
//                .neuteredImage("DataURL")
//                .description("이 친구는 어떠 어떠하며 어떠 어떠한 특성을 가지고 있고 어떠 어떠한 습관을 가지고 있어요.")
//                .boardImage1("DataURL")
//                .boardImage2("DataURL")
//                .boardImage3("DataURL")
//                .build();
//
//        mockMvc.perform(put("/api/boards")
//                .header("Authorization", token)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(boardRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("success").value(true))
//                .andExpect(jsonPath("message").value("게시글이 수정 되었습니다."))
//                .andExpect(jsonPath("data.id").exists())
//                .andExpect(jsonPath("data.title").exists())
//
//                .andDo(document("create-board",
//                        requestHeaders(
//                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
//                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
//                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
//                        ),
//                        requestFields(
//                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
//                                fieldWithPath("credit").type(JsonFieldType.NUMBER).description("껌 (보증금)"),
//                                fieldWithPath("zone").type(JsonFieldType.STRING).description("지역"),
//                                fieldWithPath("gender").type(JsonFieldType.STRING).description("성별"),
//                                fieldWithPath("year").type(JsonFieldType.NUMBER).description("나이(년)"),
//                                fieldWithPath("month").type(JsonFieldType.NUMBER).description("나이(개월)"),
//                                fieldWithPath("petTitle").type(JsonFieldType.STRING).description("품종"),
//                                fieldWithPath("checkUpImage").type(JsonFieldType.STRING).description("건강검진 이미지"),
//                                fieldWithPath("lineAgeImage").type(JsonFieldType.STRING).description("혈통서 이미지"),
//                                fieldWithPath("neuteredImage").type(JsonFieldType.STRING).description("중성화 이미지"),
//                                fieldWithPath("description").type(JsonFieldType.STRING).description("소개"),
//                                fieldWithPath("boardImage1").type(JsonFieldType.STRING).description("강아지 이미지1"),
//                                fieldWithPath("boardImage2").type(JsonFieldType.STRING).description("강아지 이미지2"),
//                                fieldWithPath("boardImage3").type(JsonFieldType.STRING).description("강아지 이미지3")
//                        ),
//                        responseHeaders(
//                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
//                        ),
//                        responseFields(
//                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
//                                fieldWithPath("message").type(JsonFieldType.STRING).description("해당 유저의 프로필 조회입니다."),
//                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("id"),
//                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목")
//                        )));
//
//    }
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
                .checkUpImage("DataURL")
                .lineAgeImage("DataURL")
                .neuteredImage("DataURL")
                .hasNeutered(true)
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
                                fieldWithPath("data.content[0].year").type(JsonFieldType.NUMBER).description("나이 (년)"),
                                fieldWithPath("data.content[0].month").type(JsonFieldType.NUMBER).description("나이 (월)"),
                                fieldWithPath("data.content[0].boardImage1").type(JsonFieldType.STRING).description("대표 이미지"),
                                fieldWithPath("data.content[0].tags").type(JsonFieldType.ARRAY).description("태그들"),
                                fieldWithPath("data.content[0].createdAt").type(JsonFieldType.STRING).description("작성일")
                        )));
    }


    @Test
    @DisplayName("입양하기 게시글 신청하기 성공/제거 테스트")
    public void adoption_board_apply_success() throws Exception {
        //given
        id = getBoardId();

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/boards/{id}/apply", this.id)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("현재 신청한 유저 여부입니다."))
                .andExpect(jsonPath("data.userIn").value(true))
                .andDo(document("adopt-apply",
                        pathParameters(
                                parameterWithName("id").description("게시글 아이디")
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("현재 신청한 유저 여부입니다."),
                                fieldWithPath("data.userIn").type(JsonFieldType.BOOLEAN).description("신청시 true 취소시 false 반환"))
                        ));
        //then
        User user = userRepository.findByNickname("백승열입니다").get();
        Board board = boardRepository.findById(id).get();
        assertTrue(board.getUsers().contains(user));
    }

    @Test
    @DisplayName("입양 게시글 수락 및 크레딧 결제")
    public void credit_accept_success() throws Exception {
        id = getBoardId();

        User user = new User();
        user.setNickname("testUser");
        user.setEmail("beck33@naver.com");
        user.setPassword("12345678");
        userRepository.save(user);


        NameDto nameDto = new NameDto("testUser");


        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/boards/{id}/accept", this.id)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nameDto)))
                .andExpect(status().isOk())
                .andDo(document("accept-credit",
                        pathParameters(
                                parameterWithName("id").description("게시글 아이디")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("껌을 채울 유저의 닉네임")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("현재 신청한 유저 목록입니다."),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("수락된 유저의 id 입니다."),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("수락된 유저의 닉네임 입니다."),
                                fieldWithPath("data.credit").type(JsonFieldType.NUMBER).description("수락된 유저의 현재 껌(크레딧) 입니다.")
                        )));

        User userResult = userRepository.findByNickname("testUser").get();
        assertEquals(userResult.getCredit(), 100000);

    }

    @Test
    @DisplayName(" 입양 게시글 주인만 신청 목록을 받는다.")
    public void adoption_apply_with_manager() throws Exception {
        id = getBoardId();


        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/boards/{id}/apply", this.id)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/boards/{id}/manager", this.id)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("현재 신청한 유저 목록입니다."))
                .andExpect(jsonPath("data.users").exists())
                .andDo(document("apply-list",
                        pathParameters(
                                parameterWithName("id").description("게시글 아이디")
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("현재 신청한 유저 목록입니다."),
                                fieldWithPath("data.users[0].id").type(JsonFieldType.NUMBER).description("현재 신청한 유저의 아이디 입니다."),
                                fieldWithPath("data.users[0].nickname").type(JsonFieldType.STRING).description("현재 신청한 유저의 닉네임 입니다."),
                                fieldWithPath("data.users[0].profileImage").type(JsonFieldType.NULL).description("현재 신청한 유저의 프로필 이미지 입니다."),
                                fieldWithPath("data.users[0].phoneNumber").type(JsonFieldType.NULL).description("현재 신청한 유저의 소개 입니다."))));
    }


    @Test
    @DisplayName("즐겨찾기 성공/제거 테스트")
    public void likeBoard_success() throws Exception {
        //given
        id = getBoardId();

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/boards/{id}/likes", this.id)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("현재 유저의 즐겨찾기 여부입니다. "))
                .andExpect(jsonPath("data.like").value(true))
                .andDo(document("like-apply",
                        pathParameters(
                                parameterWithName("id").description("게시글 아이디")
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("현재 신청한 유저 여부입니다."),
                                fieldWithPath("data.like").type(JsonFieldType.BOOLEAN).description("즐겨찾기 시 true 취소시 false 반환"))
                ));
        //then
        User user = userRepository.findByNickname("백승열입니다").get();
        Board board = boardRepository.findById(id).get();
        assertTrue(user.getLikeList().contains(board));
    }

    @Test
    @DisplayName("입양하기 글의 주인이 아니면 신청 목록을 얻지 못한다.")
    public void adoption_apply_with_no_manager() {

    }

    @Test
    @DisplayName("신청을 했는지 여부 ")
    public void Applied_me() throws Exception {
        //given
        id = getBoardId();

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/boards/{id}/applied_me", this.id)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("현재 신청하지 않은 유저입니다."))
                .andDo(document("applied_me",
                        pathParameters(
                                parameterWithName("id").description("게시글 아이디")
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
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("신청되었다면 true가 아니라면 false가 반환됩니다."),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("신청 여부의 메세지를 반환합니다.")
                )));
        
    }


}