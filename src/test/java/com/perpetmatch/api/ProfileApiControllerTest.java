package com.perpetmatch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Domain.*;
import com.perpetmatch.Domain.Item.Item;
import com.perpetmatch.Item.ItemRepository;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.Order.OrderRepository;
import com.perpetmatch.OrderItem.OrderItemRepository;
import com.perpetmatch.PetAge.PetAgeRepository;
import com.perpetmatch.Zone.ZoneRepository;
import com.perpetmatch.api.dto.Order.AddressDto;
import com.perpetmatch.api.dto.Profile.*;
import com.perpetmatch.common.RestDocsConfiguration;
import com.perpetmatch.infra.config.AppProperties;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

import static com.sun.activation.registries.LogSupport.log;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
class ProfileApiControllerTest {

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
    @Autowired
    ZoneRepository zoneRepository;
    @Autowired
    AppProperties appProperties;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;
    private String token = null;
    private Long id;

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

        id = userRepository.findByNickname("백승열입니다").get().getId();
        TokenTest findToken = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TokenTest.class);
        token = findToken.getTokenType() + " " + findToken.getAccessToken();
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @DisplayName("자신의 프로필 조회 성공 - 입력값 정상")
    @Test
    void getMyProfile_success() throws Exception {
        ProfileRequest profileRequest = new ProfileRequest();
                profileRequest.setAge(19);
                profileRequest.setOccupation("학생");
                profileRequest.setLocation("서울");
                profileRequest.setHouseType("아파트");
                profileRequest.setExperience(false);
                profileRequest.setLiveAlone(false);
                profileRequest.setHasPet(true);
                profileRequest.setProfileImage("DataURL");
                profileRequest.setPhoneNumber("010-3926-6280");
                profileRequest.setDescription("안녕하세요 누구 입니다.");
                profileRequest.setWantCheckUp(true);
                profileRequest.setWantLineAge(true);
                profileRequest.setWantNeutered(true);
                profileRequest.setExpectedFeeForMonth(100000);

        mockMvc.perform(post("/api/profiles")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileRequest)));

        ResultActions results = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/profiles")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").exists())
                .andExpect(jsonPath("data.nickname").exists())
                .andExpect(jsonPath("data.email").exists())
                .andExpect(jsonPath("data.petTitles").exists())
                .andExpect(jsonPath("data.zones").exists())
                .andExpect(jsonPath("data.joinedAt").exists())
                .andExpect(jsonPath("data.petAges").exists())
                .andExpect(jsonPath("data.credit").exists())
                .andExpect(jsonPath("data.age").exists())
                .andExpect(jsonPath("data.occupation").exists())
                .andExpect(jsonPath("data.location").exists())
                .andExpect(jsonPath("data.houseType").exists())
                .andExpect(jsonPath("data.experience").exists())
                .andExpect(jsonPath("data.liveAlone").exists())
                .andExpect(jsonPath("data.hasPet").exists())
                .andExpect(jsonPath("data.profileImage").exists())
                .andExpect(jsonPath("data.phoneNumber").exists())
                .andExpect(jsonPath("data.description").exists())
                .andExpect(jsonPath("data.wantCheckUp").exists())
                .andExpect(jsonPath("data.wantLineAge").exists())
                .andExpect(jsonPath("data.wantNeutered").exists())
                .andExpect(jsonPath("data.expectedFeeForMonth").exists());

        results.andDo(document("my-profile",
//                        preprocessRequest(modifyUris()
//                                        .scheme("https")
//                                        .host(appProperties.getHost()),
//                                prettyPrint()),

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
                        fieldWithPath("message").type(JsonFieldType.STRING).description("자신의 프로필 조회입니다."),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("ID"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                        fieldWithPath("data.joinedAt").type(JsonFieldType.STRING).description("가입일"),
                        fieldWithPath("data.petTitles").type(JsonFieldType.ARRAY).description("원하는 품종"),
                        fieldWithPath("data.zones").type(JsonFieldType.ARRAY).description("원하는 지역"),
                        fieldWithPath("data.petAges").type(JsonFieldType.ARRAY).description("원하는 나이"),
                        fieldWithPath("data.credit").type(JsonFieldType.NUMBER).description("껌 (보증금)"),
                        fieldWithPath("data.age").type(JsonFieldType.NUMBER).description("나이"),
                        fieldWithPath("data.occupation").type(JsonFieldType.STRING).description("직업"),
                        fieldWithPath("data.location").type(JsonFieldType.STRING).description("지역"),
                        fieldWithPath("data.houseType").type(JsonFieldType.STRING).description("주택"),
                        fieldWithPath("data.hasPet").type(JsonFieldType.BOOLEAN).description("반려동물을 현재 기르는지 여부 "),
                        fieldWithPath("data.experience").type(JsonFieldType.BOOLEAN).description("반려동물 키워본 경험 유무"),
                        fieldWithPath("data.liveAlone").type(JsonFieldType.BOOLEAN).description("혼자 사는지 아닌지 여부"),
                        fieldWithPath("data.profileImage").type(JsonFieldType.STRING).description("프로필 이미지"),
                        fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("간단한 자기소개"),
                        fieldWithPath("data.wantCheckUp").type(JsonFieldType.BOOLEAN).description("건강검진을 원하는지 여부"),
                        fieldWithPath("data.wantLineAge").type(JsonFieldType.BOOLEAN).description("혈통서를 원하는지 여부"),
                        fieldWithPath("data.wantNeutered").type(JsonFieldType.BOOLEAN).description("중성화가 필요한지 여부"),
                        fieldWithPath("data.expectedFeeForMonth").type(JsonFieldType.NUMBER).description("한달 지출"))
        ));

    }
    @DisplayName("프로필 조회 성공 - 입력값 정상")
    @Test
    void getOneUserProfile_success() throws Exception {

        ProfileRequest profileRequest = new ProfileRequest();
        profileRequest.setAge(19);
        profileRequest.setOccupation("학생");
        profileRequest.setLocation("서울");
        profileRequest.setHouseType("아파트");
        profileRequest.setExperience(false);
        profileRequest.setLiveAlone(false);
        profileRequest.setHasPet(true);
        profileRequest.setProfileImage("DataURL");
        profileRequest.setPhoneNumber("010-3926-6280");
        profileRequest.setDescription("안녕하세요 누구 입니다.");
        profileRequest.setWantCheckUp(true);
        profileRequest.setWantLineAge(true);
        profileRequest.setWantNeutered(true);
        profileRequest.setExpectedFeeForMonth(100000);

        mockMvc.perform(post("/api/profiles")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileRequest)));


        ResultActions results = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/profiles/{id}", id)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").exists())
                .andExpect(jsonPath("data.nickname").exists())
                .andExpect(jsonPath("data.email").exists())
                .andExpect(jsonPath("data.petTitles").exists())
                .andExpect(jsonPath("data.zones").exists())
                .andExpect(jsonPath("data.joinedAt").exists())
                .andExpect(jsonPath("data.petAges").exists())
                .andExpect(jsonPath("data.credit").exists())
                .andExpect(jsonPath("data.age").exists())
                .andExpect(jsonPath("data.occupation").exists())
                .andExpect(jsonPath("data.location").exists())
                .andExpect(jsonPath("data.houseType").exists())
                .andExpect(jsonPath("data.experience").exists())
                .andExpect(jsonPath("data.liveAlone").exists())
                .andExpect(jsonPath("data.hasPet").exists())
                .andExpect(jsonPath("data.profileImage").exists())
                .andExpect(jsonPath("data.phoneNumber").exists())
                .andExpect(jsonPath("data.description").exists())
                .andExpect(jsonPath("data.wantCheckUp").exists())
                .andExpect(jsonPath("data.wantLineAge").exists())
                .andExpect(jsonPath("data.wantNeutered").exists())
                .andExpect(jsonPath("data.expectedFeeForMonth").exists());

                results.andDo(document("show-profile",
//                        preprocessRequest(modifyUris()
//                                        .scheme("https")
//                                        .host(appProperties.getHost()),
//                                prettyPrint()),
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 유저의 프로필 조회입니다."),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("ID"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.joinedAt").type(JsonFieldType.STRING).description("가입일"),
                                fieldWithPath("data.petTitles").type(JsonFieldType.ARRAY).description("원하는 품종"),
                                fieldWithPath("data.zones").type(JsonFieldType.ARRAY).description("원하는 지역"),
                                fieldWithPath("data.petAges").type(JsonFieldType.ARRAY).description("원하는 나이"),
                                fieldWithPath("data.credit").type(JsonFieldType.NUMBER).description("껌 (보증금)"),
                                fieldWithPath("data.age").type(JsonFieldType.NUMBER).description("나이"),
                                fieldWithPath("data.occupation").type(JsonFieldType.STRING).description("직업"),
                                fieldWithPath("data.location").type(JsonFieldType.STRING).description("지역"),
                                fieldWithPath("data.houseType").type(JsonFieldType.STRING).description("주택"),
                                fieldWithPath("data.hasPet").type(JsonFieldType.BOOLEAN).description("반려동물을 현재 기르는지 여부 "),
                                fieldWithPath("data.experience").type(JsonFieldType.BOOLEAN).description("반려동물 키워본 경험 유무"),
                                fieldWithPath("data.liveAlone").type(JsonFieldType.BOOLEAN).description("혼자 사는지 아닌지 여부"),
                                fieldWithPath("data.profileImage").type(JsonFieldType.STRING).description("프로필 이미지"),
                                fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("간단한 자기소개"),
                                fieldWithPath("data.wantCheckUp").type(JsonFieldType.BOOLEAN).description("건강검진을 원하는지 여부"),
                                fieldWithPath("data.wantLineAge").type(JsonFieldType.BOOLEAN).description("혈통서를 원하는지 여부"),
                                fieldWithPath("data.wantNeutered").type(JsonFieldType.BOOLEAN).description("중성화가 필요한지 여부"),
                                fieldWithPath("data.expectedFeeForMonth").type(JsonFieldType.NUMBER).description("한달 지출"))
                    ));

    }

    @DisplayName("프로필 등록 성공 - 입력값 정상")
    @Test
    void updateProfile_success() throws Exception {

        ProfileRequest profileRequest = new ProfileRequest();
        profileRequest.setAge(19);
        profileRequest.setOccupation("학생");
        profileRequest.setLocation("서울");
        profileRequest.setHouseType("아파트");
        profileRequest.setExperience(false);
        profileRequest.setLiveAlone(false);
        profileRequest.setHasPet(true);
        profileRequest.setProfileImage("DataURL");
        profileRequest.setPhoneNumber("010-3926-6280");
        profileRequest.setDescription("안녕하세요 누구 입니다.");
        profileRequest.setWantCheckUp(true);
        profileRequest.setWantLineAge(true);
        profileRequest.setWantNeutered(true);
        profileRequest.setExpectedFeeForMonth(100000);


        mockMvc.perform(post("/api/profiles")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("프로필이 등록 완료 되었습니다."))
                .andDo(document("create-profile",
//                        preprocessRequest(modifyUris()
//                                        .scheme("http")
//                                        .host(appProperties.getHost()),
//                                prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("age").type(JsonFieldType.NUMBER).description("나이 (필수)"),
                                fieldWithPath("occupation").type(JsonFieldType.STRING).description("직업 (필수)"),
                                fieldWithPath("location").type(JsonFieldType.STRING).description("지역 (필수)"),
                                fieldWithPath("houseType").type(JsonFieldType.STRING).description("주택 (필수)"),
                                fieldWithPath("experience").type(JsonFieldType.BOOLEAN).description("반려동물 키워본 경험 유무 (필수)"),
                                fieldWithPath("liveAlone").type(JsonFieldType.BOOLEAN).description("혼자 사는지 아닌지 여부 (필수)"),
                                fieldWithPath("hasPet").type(JsonFieldType.BOOLEAN).description("현재 키우고 있는 반려동물 여부 "),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호 (필수)"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("간단한 자기소개 (필수)"),
                                fieldWithPath("profileImage").type(JsonFieldType.STRING).description("프로필 이미지"),
                                fieldWithPath("wantCheckUp").type(JsonFieldType.BOOLEAN).description("건강검진을 원하는지 여부"),
                                fieldWithPath("wantLineAge").type(JsonFieldType.BOOLEAN).description("혈통서를 원하는지 여부"),
                                fieldWithPath("wantNeutered").type(JsonFieldType.BOOLEAN).description("중성화가 필요한지 여부"),
                                fieldWithPath("expectedFeeForMonth").type(JsonFieldType.NUMBER).description("한달 지출"),
                                fieldWithPath("zones").type(JsonFieldType.ARRAY).description("유저 선호 지역"),
                                fieldWithPath("petTitles").type(JsonFieldType.ARRAY).description("유저 선호 품종"),
                                fieldWithPath("petAges").type(JsonFieldType.ARRAY).description("유저 선호 나이")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("프로필이 등록 완료 되었습니다."),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("아이디"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일")
                        )
                        ));



        User member = userRepository.findByNickname("백승열입니다").get();
        assertEquals(member.getHouseType(), "아파트");
    }

    @DisplayName("프로필 수정 실패 - 입력값 빈 값")
    @Test
    void updateProfile_failed() throws Exception {

        ProfileRequest profileRequest = new ProfileRequest();
        profileRequest.setOccupation("약사");
        profileRequest.setExperience(false);
        profileRequest.setHasPet(true);
        profileRequest.setProfileImage("DataURL");
        profileRequest.setPhoneNumber("010-3926-6280");
        profileRequest.setExpectedFeeForMonth(100000);


        mockMvc.perform(post("/api/profiles")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("입력값을 다 채우지 않았습니다."));
    }

   @Test
   @DisplayName("패스워드 수정 성공 - 입력값 정상")
    void updatePassword_success() throws Exception {

       PasswordRequest passwordRequest = new PasswordRequest();
       passwordRequest.setNewPassword("@!dighfkddd");
       passwordRequest.setNewPasswordConfirm("@!dighfkddd");


       mockMvc.perform(put("/api/profiles/password")
               .header("Authorization", token)
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(passwordRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("success").value(true))
               .andExpect(jsonPath("message").value("패스워드 수정이 완료 되었습니다."))
               .andDo(document("update-password",
//                       preprocessRequest(modifyUris()
//                                       .scheme("http")
//                                       .host(appProperties.getHost()),
//                               prettyPrint()),
                       requestHeaders(
                               headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                               headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                               headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")

                       ),
                       requestFields(
                               fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새로운 패스워드"),
                               fieldWithPath("newPasswordConfirm").type(JsonFieldType.STRING).description("새로운 패스워드 확인")
                       ),
                       responseHeaders(
                               headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                       ),
                       responseFields(
                               fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                               fieldWithPath("message").type(JsonFieldType.STRING).description("패스워드 수정이 완료 되었습니다.")
                       )
               ));
       User member = userRepository.findByNickname("백승열입니다").get();
        assertTrue(passwordEncoder.matches("@!dighfkddd", member.getPassword()));
   }

    @Test
    @DisplayName("패스워드 수정 실패 - 입력값 다름")
    void updatePassword_failed() throws Exception {

        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setNewPassword("@!dighfkddd");
        passwordRequest.setNewPasswordConfirm("@!dighfkddf");

        mockMvc.perform(put("/api/profiles/password")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("입력한 새 패스워드가 일치하지 않습니다."));
    }

    @Test
    @DisplayName(" 로그인 한 유저의 관심 품종 조회 " )
    void showPetTag_success() throws Exception {

        mockMvc.perform(get("/api/profiles/pet/title")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.petTitles").exists())
                .andExpect(jsonPath("data.allPetTitles").exists())
                .andDo(document("show-petTitle",
//                        preprocessRequest(modifyUris()
//                                        .scheme("http")
//                                        .host(appProperties.getHost()),
//                                prettyPrint()),
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("해당 유저의 선호 품종입니다."),
                                fieldWithPath("data.petTitles").type(JsonFieldType.ARRAY).description("유저의 선호 품종"),
                                fieldWithPath("data.allPetTitles").type(JsonFieldType.ARRAY).description("품종 리스트")
                        )
                ));
    }

    @Test
    @DisplayName("관심 품종 추가 성공 " )
    void addPetTag_success() throws Exception {
        //Given
        PetForm petForm = new PetForm();
        petForm.setPetTitle("푸들");

        // When
        mockMvc.perform(post("/api/profiles/pet/title")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("성공적으로 품종을 추가했습니다."))
                .andDo(document("update-petTitle",
//                        preprocessRequest(modifyUris()
//                                        .scheme("http")
//                                        .host(appProperties.getHost()),
//                                prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")

                        ),
                        requestFields(
                                fieldWithPath("petTitle").type(JsonFieldType.STRING).description("품종")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("성공적으로 품종을 추가했습니다.")
                        )));

        //Then
        Pet newPet = petRepository.findByTitle("푸들");
        assertTrue(newPet != null);
        assertTrue(userRepository.findByNickname("백승열입니다").get().getPetTitles().contains(newPet));


    }

    @Test
    @DisplayName("관심 품종 추가 실패 " )
    void removePetTag_failed() throws Exception {
        User member = userRepository.findByNickname("백승열입니다").get();
        PetForm petForm = new PetForm();
        petForm.setPetTitle("말티즈");
        Pet pet = petRepository.save(Pet.builder().title(petForm.getPetTitle()).build());
        userService.addPet(member.getId(),pet.getTitle());

        assertTrue(member.getPetTitles().contains(pet));

        mockMvc.perform(delete("/api/profiles/pet/title")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petForm)))
                .andExpect(status().isOk())
                .andDo(document("remove-petTitle",
//                        preprocessRequest(modifyUris()
//                                        .scheme("http")
//                                        .host(appProperties.getHost()),
//                                prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")

                        ),
                        requestFields(
                                fieldWithPath("petTitle").type(JsonFieldType.STRING).description("품종")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("성공적으로 품종을 제거했습니다.")
                        )));

        assertTrue(!member.getPetTitles().contains(pet));
    }

    @Test
    @DisplayName(" 로그인 한 유저의 관심 펫 나이 조회 " )
    void showPetAges_success() throws Exception {

        mockMvc.perform(get("/api/profiles/pet/age")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.ages").exists())
                .andExpect(jsonPath("data.allAges").exists())
                .andDo(document("show-petAge",
//                        preprocessRequest(modifyUris()
//                                        .scheme("https")
//                                        .host("perpetapi.com")
//                                prettyPrint()),
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("해당 유저의 선호 나이입니다."),
                                fieldWithPath("data.ages").type(JsonFieldType.ARRAY).description("선호 나이 ( 리스트 중 택 1 )"),
                                fieldWithPath("data.allAges").type(JsonFieldType.ARRAY).description("선호 나이 리스트")
                        )));
    }
    @Test
    @DisplayName("관심 나이 추가 성공 " )
    void addPetAge_success() throws Exception {
        PetAgeRequest petAge = new PetAgeRequest();
        petAge.setPetRange("1년이하");

        mockMvc.perform(post("/api/profiles/pet/age")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petAge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("성공적으로 나이를 추가했습니다."))
                .andDo(document("update-petAge",
//                        preprocessRequest(modifyUris()
//                                        .scheme("http")
//                                        .host(appProperties.getHost()),
//                                prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")

                        ),
                        requestFields(
                                fieldWithPath("petRange").type(JsonFieldType.STRING).description("추가할 선호 나이 ( 리스트 중 택 1 )")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("성공적으로 나이를 추가했습니다.")
                        )));

        PetAge petRange = petAgeRepository.findPetRange("1년이하");
        assertTrue(userRepository.findByNickname("백승열입니다").get().getPetAges().contains(petRange));

    }
    @Test
    @DisplayName("관심 나이 제거 성공 " )
    void addPetAge_failed() throws Exception {
        PetAgeRequest petAge = new PetAgeRequest();
        petAge.setPetRange("1년이하");

        mockMvc.perform(delete("/api/profiles/pet/age")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petAge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("성공적으로 나이를 제거했습니다."))
                .andDo(document("remove-petAge",
//                        preprocessRequest(modifyUris()
//                                        .scheme("http")
//                                        .host(appProperties.getHost()),
//                                prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")

                        ),
                        requestFields(
                                fieldWithPath("petRange").type(JsonFieldType.STRING).description("제거할 선호 나이 ( 리스트 중 택 1 )")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("성공적으로 나이를 제거했습니다.")
                        )));

        PetAge petRange = petAgeRepository.findPetRange("1년이하");
        assertTrue(!userRepository.findByNickname("백승열입니다").get().getPetAges().contains(petRange));

    }


    @Test
    @DisplayName(" 로그인 한 유저의 관심 지역 조회 " )
    void showZone_success() throws Exception {

        mockMvc.perform(get("/api/profiles/zone")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.zones").exists())
                .andExpect(jsonPath("data.allZones").exists())
                .andDo(document("show-zone",
//                        preprocessRequest(modifyUris()
//                                        .scheme("http")
//                                        .host(appProperties.getHost()),
//                                prettyPrint()),
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("해당 유저의 선호 지역입니다."),
                                fieldWithPath("data.zones").type(JsonFieldType.ARRAY).description("선호 지역"),
                                fieldWithPath("data.allZones").type(JsonFieldType.ARRAY).description("선호 지역 리스트")
                        )));
    }

    @Test
    @DisplayName("관심 지역 추가 성공 " )
    void addZone_success() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setProvince("서울특별시");

        mockMvc.perform(post("/api/profiles/zone")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("성공적으로 지역을 추가했습니다."))
                .andDo(document("update-petZone",
//                        preprocessRequest(modifyUris()
//                                        .scheme("http")
//                                        .host(appProperties.getHost()),
//                                prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")

                        ),
                        requestFields(
                                fieldWithPath("province").type(JsonFieldType.STRING).description("추가할 선호 지역들")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("성공적으로 지역을 추가했습니다."))
                        ));

        Zone zone = zoneRepository.findByProvince("서울특별시");
        assertTrue(userRepository.findByNickname("백승열입니다").get().getZones().contains(zone));

    }
    @Test
    @DisplayName("관심 나이 제거 성공 " )
    void addZone_failed() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setProvince("서울특별시");

        mockMvc.perform(delete("/api/profiles/zone")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("성공적으로 지역을 제거했습니다."))
                .andDo(document("remove-petZone",
//                        preprocessRequest(modifyUris()
//                                        .scheme("http")
//                                        .host(appProperties.getHost()),
//                                prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("JSON"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("JSON"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")

                        ),
                        requestFields(
                                fieldWithPath("province").type(JsonFieldType.STRING).description("제거할 선호 지역")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type 헤더")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("true"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("성공적으로 지역을 제거했습니다.")
                        )));

        Zone zone = zoneRepository.findByProvince("서울특별시");
        assertTrue(!userRepository.findByNickname("백승열입니다").get().getZones().contains(zone));

    }
    
    @Test
    @DisplayName("자신의 껌(크레딧) 조회하기")
    void getCreditMyself() throws Exception {
        mockMvc.perform(get("/api/profiles/credit")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("자신의 껌입니다."))
                .andDo(document("get-credit",
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("자신의 껌입니다."),
                                fieldWithPath("data.credit").type(JsonFieldType.NUMBER).description("자신의 껌(크레딧)입니다.")
                        )));

    }

    @Test
    @DisplayName("마이페이지 프로필 조회")
    public void getMyPageProfile() throws Exception{

        User user = userRepository.findById(id).get();
        user.setAge(19);
        user.setOccupation("학생");
        user.setLocation("서울");
        user.setHouseType("아파트");
        user.setExperience(false);
        user.setLiveAlone(false);
        user.setHasPet(true);
        user.setProfileImage("DataURL");
        user.setPhoneNumber("010-3926-6280");
        user.setDescription("안녕하세요 누구 입니다.");
        user.setWantCheckUp(true);
        user.setWantLineAge(true);
        user.setWantNeutered(true);
        user.setExpectedFeeForMonth(100000);

        mockMvc.perform(get("/api/profiles/mypage/{id}", this.id)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("요청 유저의 마이페이지 프로필 조회입니다."))
                .andDo(document("mypage-profile",
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 유저의 마이페이지 프로필 조회입니다."),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.profileImage").type(JsonFieldType.STRING).description("프로필 이미지"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("짧은소개"),
                                fieldWithPath("data.tags").type(JsonFieldType.ARRAY).description("유저의 프로필 첫 페이지 태그 입니다."),
                                fieldWithPath("data.credit").type(JsonFieldType.NUMBER).description("유저의 크레딧 입니다.")
                        )));

    }

    @Test
    @DisplayName("마이페이지 주문내역 조회")
    public void getMyPageOrder() throws Exception{
        Long bagId = 286L;

        User userId = userRepository.findByNickname("백승열입니다").get();
        User user = userRepository.findByIdWithBags(userId.getId());
        user.setCredit(100000);
        Item item = itemRepository.findById(bagId).get();
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(item.getPrice());
        orderItem.setCount(3);
        orderItemRepository.save(orderItem);

        user.getBag().add(orderItem);

        AddressDto addressDto = new AddressDto();
        addressDto.setDear("홍길동");
        addressDto.setZipcode("12395");
        addressDto.setCity("서울 OO구 OO동");
        addressDto.setStreet("54-30");
        addressDto.setMemo("부재시 경비실에 맡겨주세요");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/order/bags/pay")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDto)));

        mockMvc.perform(get("/api/profiles/mypage/orders/{id}", this.id)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("요청 유저의 마이페이지 주문 조회입니다."))
                .andDo(document("mypage-orders",
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
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 유저의 마이페이지 주문내역 조회입니다."),
                                fieldWithPath("data[0].id").type(JsonFieldType.NUMBER).description("주문 아이디"),
                                fieldWithPath("data[0].image").type(JsonFieldType.STRING).description("상품 이미지"),
                                fieldWithPath("data[0].title").type(JsonFieldType.STRING).description("상품 이름"),
                                fieldWithPath("data[0].price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("data[0].company").type(JsonFieldType.STRING).description("상품 회사"),
                                fieldWithPath("data[0].count").type(JsonFieldType.NUMBER).description("주문 갯수"),
                                fieldWithPath("data[0].orderDate").type(JsonFieldType.STRING).description("주문 날짜")
                        )));

    }
}