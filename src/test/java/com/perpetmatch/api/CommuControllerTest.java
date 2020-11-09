package com.perpetmatch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Comment.CommentRepository;
import com.perpetmatch.Commu.CommuRepository;
import com.perpetmatch.Domain.Comment;
import com.perpetmatch.Domain.Commu;
import com.perpetmatch.Domain.User;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.api.dto.Commu.CommentDto;
import com.perpetmatch.api.dto.Commu.CommuPostDto;
import com.perpetmatch.common.RestDocsConfiguration;
import com.perpetmatch.jjwt.resource.LoginRequest;
import com.perpetmatch.jjwt.resource.SignUpRequest;
import lombok.extern.slf4j.Slf4j;
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

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class CommuControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommuRepository commuRepository;
    @Autowired
    CommentRepository commentRepository;


    private Long id;
    private String token = null;

    @BeforeEach
    void beforeEach() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .nickname("백승열입니다")
                .email("beck22222@naver.com")
                .password("@!dighfkddl").build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("beck22222@naver.com")
                .password("@!dighfkddl")
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()).andReturn();

        TokenTest findToken = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TokenTest.class);
        token = findToken.getTokenType() + " " + findToken.getAccessToken();
    }

    @Test
    @DisplayName("소통 게시판 만들기 성공 테스트")
    public void createCommuBoard() throws Exception {
        CommuPostDto commuPostDto = CommuPostDto.builder()
                .checked(false)
                .image("DataURL")
                .description("제 강아지 어때요??")
                .likes(0).build();

        mockMvc.perform(post("/api/commu/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commuPostDto)))
                .andExpect(status().isOk())
                .andDo(document("create-commuBoard",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("checked").type(JsonFieldType.BOOLEAN).description("인공지능 글 여부"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("사진"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("글"),
                                fieldWithPath("likes").type(JsonFieldType.NUMBER).description("좋아요")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("소통 게시글이 등록 되었습니다."))));
    }

    @Test
    @DisplayName("소통 댓글 등록")
    public void getCommuComments() throws Exception {

        Event event = getBoardId();
        Long userId = event.userId;
        Long Id = event.CommuId;

        CommentDto dto = new CommentDto("제 강아지좀 봐주세요.");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/commu/boards/{Id}/comments", Id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("create-text",
                        pathParameters(
                                parameterWithName("Id").description("소통 게시글 아이디")
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("성공적으로 댓글을 추가했습니다."))));
    }

    @Test
    @DisplayName("소통 댓글 삭제")
    public void removeCommuComments() throws Exception {

        Event event = getBoardId();
        Long userId = event.userId;
        Long Id = event.CommuId;
        Long commentsId = event.CommentId;

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/commu/boards/{Id}/comments/{commentsId}", Id, commentsId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("delete-text",
                        pathParameters(
                                parameterWithName("Id").description("소통 게시글 아이디"),
                                parameterWithName("commentsId").description("댓글 아이디")
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("성공적으로 댓글을 제거했습니다."))));
    }

    @Test
    @DisplayName("해당 게시글에 소통 댓글 조회")
    public void getCommuCOmments() throws Exception {

        Event event = getBoardId();
        Long userId = event.userId;
        Long Id = event.CommuId;
        Long commentsId = event.CommentId;

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/commu/boards/{Id}/comments", Id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("text-list",
                        pathParameters(
                                parameterWithName("Id").description("소통 게시글 아이디")
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("소통 댓글 리스트 입니다."),
                                fieldWithPath("data[0].id").type(JsonFieldType.NUMBER).description("소통 댓글 리스트 입니다."),
                                fieldWithPath("data[0].nickname").type(JsonFieldType.STRING).description("소통 댓글 리스트 입니다."),
                                fieldWithPath("data[0].profileImage").type(JsonFieldType.STRING).description("소통 댓글 리스트 입니다."),
                                fieldWithPath("data[0].text").type(JsonFieldType.STRING).description("소통 댓글 리스트 입니다."))));
    }
    @Test
    @DisplayName("좋아요")
    public void AddLikes() throws Exception {
        Event event = getBoardId();
        Long userId = event.userId;
        Long Id = event.CommuId;

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/commu/boards/{Id}/likes", Id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("add-likes",
                        pathParameters(
                                parameterWithName("Id").description("소통 게시글 아이디")
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("좋아요"))));

    }

    @Test
    @DisplayName("소통 게시글 조회 조회")
    public void getCommuBoard() throws Exception {

        Event event = getBoardId();
        Long userId = event.userId;
        Long Id = event.CommuId;
        Long commentsId = event.CommentId;

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/commu/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("commu-list",
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("소통 댓글 리스트 입니다."),
                                fieldWithPath("data[0].id").type(JsonFieldType.NUMBER).description("소통 글 ID"),
                                fieldWithPath("data[0].nickname").type(JsonFieldType.STRING).description("글 작성자 닉네임"),
                                fieldWithPath("data[0].profileImage").type(JsonFieldType.STRING).description("게시글 작성자 프로필 이미지"),
                                fieldWithPath("data[0].description").type(JsonFieldType.STRING).description("글"),
                                fieldWithPath("data[0].createdAt").type(JsonFieldType.STRING).description("생성 날짜"),
                                fieldWithPath("data[0].likes").type(JsonFieldType.NUMBER).description("좋아요"),
                                fieldWithPath("data[0].image").type(JsonFieldType.STRING).description("이미지"),
                                fieldWithPath("data[0].checked").type(JsonFieldType.BOOLEAN).description("인공지능 글 여부"),
                                fieldWithPath("data[0].comments[0].id").type(JsonFieldType.NUMBER).description("댓글 아이디"),
                                fieldWithPath("data[0].comments[0].nickname").type(JsonFieldType.STRING).description("댓글 작성자 닉네임"),
                                fieldWithPath("data[0].comments[0].profileImage").type(JsonFieldType.STRING).description("댓글 작성자 이미지"),
                                fieldWithPath("data[0].comments[0].text").type(JsonFieldType.STRING).description("댓글"))));
    }

    private Event getBoardId() throws Exception {

        User user = userRepository.findByNickname("백승열입니다").get();
        user.setProfileImage("DataURL");
        Commu commu = new Commu();
        commu.setNickname(user.getNickname());
        commu.setDescription("제 강아지좀 보세요");
        commu.setLikes(1);
        commu.setImage("DataURL");
        commu.setChecked(false);
        commu.setProfileImage("DataURL");

        Comment comment = new Comment();
        comment.setNickname(user.getNickname());
        comment.setProfileImage(user.getProfileImage());
        comment.setText("Test");
        commentRepository.save(comment);
        commu.getComments().add(comment);
        commuRepository.save(commu);
        user.getCommus().add(commu);
        return new Event(user.getId(),commu.getId(),comment.getId());
    }
    private static class Event {
        Long userId;
        Long CommuId;
        Long CommentId;

        public Event(Long userId, Long commuId, Long commentId) {
            this.userId = userId;
            CommuId = commuId;
            CommentId = commentId;
        }
    }

}