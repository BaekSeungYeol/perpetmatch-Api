package com.perpetmatch.api;

import com.perpetmatch.Domain.Member;
import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.PetAge;
import com.perpetmatch.Member.MemberRepository;
import com.perpetmatch.Member.MemberService;
import com.perpetmatch.PetAge.PetAgeRepository;
import com.perpetmatch.apiDto.Profile.*;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.pet.PetRepository;
import com.perpetmatch.pet.PetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 마이 페이지에 프로필 수정 눌렀을때
 *  프로필
 *  패스워드
 *  관심 펫
 *  지역
 *  계정 탈퇴
 */
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class ProfileApiController {

    private final PetService petService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PetRepository petRepository;
    private final PetAgeRepository petAgeRepository;


    // 이름으로 유저 한명의 프로필 조회
    @GetMapping("/profile/one")
    public ResponseEntity<ProfileResponse> profileAll(@CurrentMember UserPrincipal currentMember) {

        if (!memberRepository.existsByNickname(currentMember.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        Member byNickname = memberService.findByNickname(currentMember.getUsername());
        return ResponseEntity.ok().body(new ProfileResponse(byNickname));
    }

    // 해당 유저의 프로필 수정
    @PostMapping("/profile/one")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity profileUpdate(@CurrentMember UserPrincipal currentMember,
                                        @Valid @RequestBody ProfileRequest profileRequest, Errors errors) {

        if (currentMember == null) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        if (errors.hasErrors()) {
            return new ResponseEntity(new ApiResponse(false, "입력값을 다 채우지 않았습니다."),
                    HttpStatus.BAD_REQUEST);
        }

        memberService.updateProfile(currentMember.getId(), profileRequest);

        return ResponseEntity.ok().body(new ApiResponse(true, "프로필이 수정 완료 되었습니다."));
    }

    // 패스워드 변경
    @PostMapping("/password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity passwordUpdate(@CurrentMember UserPrincipal currentMember, @RequestBody @Valid
            PasswordRequest passwordRequest, Errors errors) {

        if (currentMember == null) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        if (!passwordRequest.getNewPassword().equals(passwordRequest.getNewPasswordConfirm())) {
            return new ResponseEntity(new ApiResponse(false, "입력한 새 패스워드가 일치하지 않습니다."),
                    HttpStatus.BAD_REQUEST);
        }

        if (errors.hasErrors()) {
            return new ResponseEntity(new ApiResponse(false, "길이가 너무 짧거나 깁니다."),
                    HttpStatus.BAD_REQUEST);
        }

        memberService.updatePassword(currentMember.getId(), passwordRequest);

        return ResponseEntity.ok().body(new ApiResponse(true, "패스워드 수정이 완료 되었습니다."));
    }

    @PostMapping("/pet/title/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity addPet(@CurrentMember UserPrincipal currentMember, @RequestBody PetForm petForm) {
        if (currentMember == null) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        String title = petForm.getPetTitle();

        memberService.addPet(currentMember.getId(), title);

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 품종을 추가했습니다."));

    }

    @PostMapping("/pet/title/remove")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity removePet(@CurrentMember UserPrincipal currentMember, @RequestBody PetForm petForm) {

        if (currentMember == null) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        String title = petForm.getPetTitle();

        Pet pet = petRepository.findByTitle(title);
        if (pet == null) {
            return new ResponseEntity(new ApiResponse(false, "해당 품종이 없습니다."),
                    HttpStatus.BAD_REQUEST);
        }

        memberService.removePet(currentMember.getId(), pet);

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 품종을 제거했습니다."));

    }


    // 해당 유저의 선호 품종을 모두 조회
    @GetMapping("/pet/title")
    public ResponseEntity getPets(@CurrentMember UserPrincipal currentMember) {

        if (!memberRepository.existsByNickname(currentMember.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }


        Member member = memberRepository.findById(currentMember.getId()).get();

        // 모든 유저의 선호 품종 리스트로 반환
        List<String> allPets = petRepository.findAll().stream().map(Pet::getTitle).collect(Collectors.toList());

        // 현재 유저의 선호 품종
        Set<PetDto> pets = member.getPet().stream().map(m -> new PetDto(m.getTitle())).collect(Collectors.toSet());
        List<String> pet = pets.stream().map(PetDto::toString).collect(Collectors.toList());
        PetResponseOne collect = new PetResponseOne(pet, allPets);

        return ResponseEntity.ok().body(collect);
    }

     // 해당 유저의 선호 나이를 조회
    @GetMapping("pet/age")
    public ResponseEntity getPetAge(@CurrentMember UserPrincipal currentMember) {

        if (!memberRepository.existsByNickname(currentMember.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        Member member = memberRepository.findById(currentMember.getId()).get();
        // 현재 유저의 선호 나이
        Set<PetDto> age = member.getPetAge().stream().map(m -> new PetDto(m.getPetRange())).collect(Collectors.toSet());
        List<String> ages = age.stream().map(PetDto::toString).collect(Collectors.toList());

        // 3가지의 선택 사항들
        List<String> allAges = petAgeRepository.findAll().stream().map(PetAge::toString).collect(Collectors.toList());

        PetAgeResponseOne collect = new PetAgeResponseOne(ages,allAges);

        return ResponseEntity.ok().body(collect);
    }

    @PostMapping("/pet/age/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity addPetAge(@CurrentMember UserPrincipal currentMember, @RequestBody PetAgeRequest petAgeRequest) {

        if (currentMember == null) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        String range = petAgeRequest.getPetRange();

        memberService.addPetAge(currentMember.getId(), range);

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 나이를 추가했습니다."));

    }
    @PostMapping("/pet/age/remove")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity removePetAge(@CurrentMember UserPrincipal currentMember, @RequestBody PetAgeRequest petAgeRequest) {

        if (currentMember == null) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        memberService.removePetAge(currentMember.getId(), petAgeRequest.getPetRange());

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 나이를 제거했습니다."));

    }
}