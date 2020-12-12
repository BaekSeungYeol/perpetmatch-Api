package com.perpetmatch.api;

import com.perpetmatch.modules.Board.BoardRepository;
import com.perpetmatch.Domain.*;
import com.perpetmatch.modules.Member.UserRepository;
import com.perpetmatch.modules.Member.UserService;
import com.perpetmatch.modules.Order.OrderService;
import com.perpetmatch.modules.PetAge.PetAgeRepository;
import com.perpetmatch.modules.Zone.ZoneRepository;
import com.perpetmatch.api.dto.Board.AdoptBoardV1;
import com.perpetmatch.api.dto.Order.MyPageDetailsDto;
import com.perpetmatch.api.dto.Profile.*;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import com.perpetmatch.modules.pet.PetRepository;
import com.perpetmatch.modules.pet.PetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 마이 페이지에 프로필 수정 눌렀을때
 *  프로필
 *  비밀번호 변경
 *  관심 품종
 *  선호 나이
 */
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Transactional
public class ProfileApiController {

    private final ZoneRepository zoneRepository;
    private final PetService petService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PetRepository petRepository;
    private final PetAgeRepository petAgeRepository;
    private final OrderService orderService;
    private final BoardRepository boardRepository;



    @GetMapping("")
    public ResponseEntity profileMe(@CurrentMember UserPrincipal currentMember) {
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        User byNickname = userService.findByParamId(currentMember.getId());

        ProfileResponse profileResponse = new ProfileResponse(byNickname);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "자신의 프로필 조회입니다.", profileResponse));


    }
    // 이름으로 유저 한명의 프로필 조회
    @GetMapping("/{id}")
    public ResponseEntity profileAll(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        User byNickname = userService.findByParamId(id);

        ProfileResponse profileResponse = new ProfileResponse(byNickname);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "요청 유저의 프로필 조회입니다.", profileResponse));
    }


    // 해당 유저의 프로필 수정
    @PostMapping("")
    public ResponseEntity profileUpdate(@CurrentMember UserPrincipal currentMember,
                                        @Valid @RequestBody ProfileRequest profileRequest, Errors errors) {

        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        if (errors.hasErrors()) {
            return new ResponseEntity<>(new ApiResponse(false, "입력값을 다 채우지 않았습니다."),
                    HttpStatus.BAD_REQUEST);
        }

        User member = userService.updateProfile(currentMember.getId(), profileRequest);
        ProfileDto profileDto = ProfileDto.builder().id(member.getId()).nickname(member.getNickname()).email(member.getEmail()).build();

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "프로필이 등록 완료 되었습니다.",profileDto));
    }

    // 패스워드 변경
    @PutMapping("/password")
    public ResponseEntity passwordUpdate(@CurrentMember UserPrincipal currentMember, @RequestBody @Valid
            PasswordRequest passwordRequest, Errors errors) {

        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        if (!passwordRequest.getNewPassword().equals(passwordRequest.getNewPasswordConfirm())) {
            return new ResponseEntity<>(new ApiResponse(false, "입력한 새 패스워드가 일치하지 않습니다."),
                    HttpStatus.BAD_REQUEST);
        }

        if (errors.hasErrors()) {
            return new ResponseEntity<>(new ApiResponse(false, "길이가 너무 짧거나 깁니다."),
                    HttpStatus.BAD_REQUEST);
        }

        userService.updatePassword(currentMember.getId(), passwordRequest);

        return ResponseEntity.ok().body(new ApiResponse(true, "패스워드 수정이 완료 되었습니다."));
    }

    @GetMapping("/zone")
    public ResponseEntity getZones(@CurrentMember UserPrincipal currentMember) {

        if (!userRepository.existsByNickname(currentMember.getUsername())) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        User member = userRepository.findById(currentMember.getId()).get();

        // 지역 리스트로 반환
        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());

        // 현재 유저의 선호 지역

        List<ZoneForm> zone = member.getZones().stream().map(z -> new ZoneForm(z.getProvince())).collect(Collectors.toList());
        List<String> zones = zone.stream().map(ZoneForm::toString).collect(Collectors.toList());
        ZoneResponseOne collect = new ZoneResponseOne(zones,allZones);

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true,"해당 유저의 지역입니다.", collect));
    }

    @PostMapping("/zone")
    public ResponseEntity addZone(@CurrentMember UserPrincipal currentMember, @RequestBody ZoneForm zoneForm) {
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        String province = zoneForm.getProvince();

        userService.addZone(currentMember.getId(), province);

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 지역을 추가했습니다."));

    }

    @DeleteMapping("/zone")
    public ResponseEntity removeZone(@CurrentMember UserPrincipal currentMember, @RequestBody ZoneForm zoneForm) {

        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        String province = zoneForm.getProvince();

        userService.removeZone(currentMember.getId(), province);

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 지역을 제거했습니다."));

    }

    @PostMapping("/pet/title")
    public ResponseEntity addPet(@CurrentMember UserPrincipal currentMember, @RequestBody PetForm petForm) {
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        String title = petForm.getPetTitle();

        userService.addPet(currentMember.getId(), title);

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 품종을 추가했습니다."));

    }

    @DeleteMapping("/pet/title")
    public ResponseEntity removePet(@CurrentMember UserPrincipal currentMember, @RequestBody PetForm petForm) {

        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        String title = petForm.getPetTitle();

        Pet pet = petRepository.findByTitle(title);
        if (pet == null) {
            return new ResponseEntity<>(new ApiResponse(false, "해당 품종이 없습니다."),
                    HttpStatus.BAD_REQUEST);
        }

        userService.removePet(currentMember.getId(), pet);

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 품종을 제거했습니다."));

    }


    // 해당 유저의 선호 품종을 모두 조회
    @GetMapping("/pet/title")
    public ResponseEntity getPets(@CurrentMember UserPrincipal currentMember) {

        if (!userRepository.existsByNickname(currentMember.getUsername())) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }


        User member = userRepository.findById(currentMember.getId()).get();

        // 모든 유저의 선호 품종 리스트로 반환
        List<String> allPets = petRepository.findAll().stream().map(Pet::toString).collect(Collectors.toList());

        // 현재 유저의 선호 품종
        Set<PetDto> pets = member.getPetTitles().stream().map(m -> new PetDto(m.getTitle())).collect(Collectors.toSet());
        List<String> pet = pets.stream().map(PetDto::toString).collect(Collectors.toList());
        PetResponseOne collect = new PetResponseOne(pet, allPets);


        return ResponseEntity.ok().body(new ApiResponseWithData<>(true,"해당 유저의 선호 품종입니다.", collect));
    }

     // 해당 유저의 선호 나이를 조회
    @GetMapping("pet/age")
    public ResponseEntity getPetAge(@CurrentMember UserPrincipal currentMember) {

        if (!userRepository.existsByNickname(currentMember.getUsername())) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        User member = userRepository.findById(currentMember.getId()).get();
        // 현재 유저의 선호 나이
        Set<PetDto> age = member.getPetAges().stream().map(m -> new PetDto(m.getPetRange())).collect(Collectors.toSet());
        List<String> ages = age.stream().map(PetDto::toString).collect(Collectors.toList());

        // 3가지의 선택 사항들
        List<String> allAges = petAgeRepository.findAll().stream().map(PetAge::toString).collect(Collectors.toList());

        PetAgeResponseOne collect = new PetAgeResponseOne(ages,allAges);

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true,"해당 유저의 선호 나이입니다.", collect));
    }

    @PostMapping("/pet/age")
    public ResponseEntity addPetAge(@CurrentMember UserPrincipal currentMember, @RequestBody PetAgeRequest petAgeRequest) {

        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        String range = petAgeRequest.getPetRange();

        userService.addPetAge(currentMember.getId(), range);

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 나이를 추가했습니다."));

    }

    @DeleteMapping("/pet/age")
    public ResponseEntity removePetAge(@CurrentMember UserPrincipal currentMember, @RequestBody PetAgeRequest petAgeRequest) {

        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        userService.removePetAge(currentMember.getId(), petAgeRequest.getPetRange());

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 나이를 제거했습니다."));

    }

    @GetMapping("/credit")
    public ResponseEntity getCreditMyself(@CurrentMember UserPrincipal currentMember) {
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findById(currentMember.getId()).get();
        CreditDto dto = new CreditDto(user.getCredit());
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "자신의 껌입니다.",dto));
    }

    @GetMapping("/mypage/{id}")
    public ResponseEntity myPageUpward(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        User user = userService.findByParamId(id);

        MyPage dto = new MyPage(user);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "요청 유저의 마이페이지 프로필 조회입니다.", dto));
    }

    @GetMapping("/mypage/orders/{id}")
    public ResponseEntity MyPageOrderDetails(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        Set<MyPageDetailsDto> orders = userService.findOrders(id);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "요청 유저의 마이페이지 주문 조회입니다.", orders));
    }

    //GET api/profiles/mypage/myboard
    @GetMapping("/mypage/boards/{id}")
    public ResponseEntity searchBoard(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(id).get();
        List<Board> boards = boardRepository.findWithManager(user);
        List<AdoptBoardV1> changed = boards.stream().map(AdoptBoardV1::new).collect(Collectors.toList());
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "파양 게시판 마이페이지 검색입니다.",changed));
    }
}