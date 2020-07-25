package com.perpetmatch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Domain.Member;
import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.PetAge;
import com.perpetmatch.Member.MemberRepository;
import com.perpetmatch.Member.MemberService;
import com.perpetmatch.PetAge.PetAgeRepository;
import com.perpetmatch.apiDto.Profile.PasswordRequest;
import com.perpetmatch.apiDto.Profile.PetAgeRequest;
import com.perpetmatch.apiDto.Profile.PetForm;
import com.perpetmatch.apiDto.Profile.ProfileRequest;
import com.perpetmatch.jjwt.resource.LoginRequest;
import com.perpetmatch.jjwt.resource.SignUpRequest;
import com.perpetmatch.pet.PetRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static com.sun.activation.registries.LogSupport.log;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PetRepository petRepository;
    @Autowired
    MemberService memberService;
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
        memberRepository.deleteAll();
    }


    @DisplayName("프로필 수정 성공 - 입력값 정상")
    @Test
    void updateProfile_success() throws Exception {


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

        mockMvc.perform(post("/api/settings/profile/one")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("프로필이 수정 완료 되었습니다."));

        Member member = memberRepository.findByNickname("백승열입니다").get();
        assertEquals(member.getHouseType(), "아파트");
    }
    @DisplayName("프로필 수정 실패 - 입력값 빈 값")
    @Test
    void updateProfile_failed() throws Exception {
        ProfileRequest profileRequest = ProfileRequest.builder()
                .occupation("약사")
                .experience(false)
                .howManyPets(2)
                .expectedFeeForMonth(100000)
                .profileImage("fdklfkdslfksdflks")
                .phoneNumber("010-3926-6280")
                .build();

        mockMvc.perform(post("/api/settings/profile/one")
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
       passwordRequest.setNewPassword("123456789");
       passwordRequest.setNewPasswordConfirm("123456789");

        mockMvc.perform(post("/api/settings/password")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("패스워드 수정이 완료 되었습니다."));

        Member member = memberRepository.findByNickname("백승열입니다").get();
        assertTrue(passwordEncoder.matches("123456789", member.getPassword()));
   }

    @Test
    @DisplayName("패스워드 수정 실패 - 입력값 다름")
    void updatePassword_failed() throws Exception {

        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setNewPassword("123456789");
        passwordRequest.setNewPasswordConfirm("12345678");

        mockMvc.perform(post("/api/settings/password")
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

        mockMvc.perform(get("/api/settings/pet/title")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("petTitles").exists())
                .andExpect(jsonPath("allPetTitles").exists());
    }

    @Test
    @DisplayName("관심 품종 추가 성공 " )
    void addPetTag_success() throws Exception {
        PetForm petForm = new PetForm();
        petForm.setPetTitle("푸들");

        mockMvc.perform(post("/api/settings/pet/title/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("성공적으로 품종을 추가했습니다."));

        Pet newPet = petRepository.findByTitle("푸들");
        assertTrue(newPet != null);
        assertTrue(memberRepository.findByNickname("백승열입니다").get().getPet().contains(newPet));


    }

    @Test
    @DisplayName("관심 품종 추가 실패 " )
    void removePetTag_failed() throws Exception {
        Member member = memberRepository.findByNickname("백승열입니다").get();
        PetForm petForm = new PetForm();
        petForm.setPetTitle("newPet");
        Pet pet = petRepository.save(Pet.builder().title(petForm.getPetTitle()).build());
        memberService.addPet(member.getId(),pet.getTitle());

        assertTrue(member.getPet().contains(pet));

        mockMvc.perform(post("/api/settings/pet/title/remove")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petForm)))
                .andExpect(status().isOk());

        assertTrue(!member.getPet().contains(pet));
    }

    @Test
    @DisplayName(" 로그인 한 유저의 관심 펫 나이 조회 " )
    void showPetAges_success() throws Exception {

        mockMvc.perform(get("/api/settings/pet/age")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("ages").exists())
                .andExpect(jsonPath("allAges").exists());
    }
    @Test
    @DisplayName("관심 나이 추가 성공 " )
    void addPetAge_success() throws Exception {
        PetAgeRequest petAge = new PetAgeRequest();
        petAge.setPetRange("1년이하");

        mockMvc.perform(post("/api/settings/pet/age/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petAge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("성공적으로 나이를 추가했습니다."));

        PetAge petRange = petAgeRepository.findPetRange("1년이하");
        assertTrue(memberRepository.findByNickname("백승열입니다").get().getPetAge().contains(petRange));

    }
    @Test
    @DisplayName("관심 나이 제거 성공 " )
    void addPetAge_failed() throws Exception {
        PetAgeRequest petAge = new PetAgeRequest();
        petAge.setPetRange("1년이하");

        mockMvc.perform(post("/api/settings/pet/age/remove")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petAge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("성공적으로 나이를 제거했습니다."));

        PetAge petRange = petAgeRepository.findPetRange("1년이하");
        assertTrue(!memberRepository.findByNickname("백승열입니다").get().getPetAge().contains(petRange));

    }
}