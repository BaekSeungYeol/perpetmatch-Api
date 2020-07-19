package com.perpetmatch.api;

import com.perpetmatch.Domain.Member;
import com.perpetmatch.Member.MemberRepository;
import com.perpetmatch.Member.MemberService;
import com.perpetmatch.apiDto.PasswordRequest;
import com.perpetmatch.apiDto.ProfileRequest;
import com.perpetmatch.apiDto.ProfileResponse;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.pet.PetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 마이 페이지 눌렀을때
 *  프로필
 *  패스워드
 *  관심 펫
 *  지역
 */
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class ProfileApiController {

    private final PetService petService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;


    // 이름으로 유저 한명의 프로필 조회
    @GetMapping("/profile/one")
    public ResponseEntity<ProfileResponse> profileAll(@CurrentMember UserPrincipal currentMember) {

        if(!memberRepository.existsByNickname(currentMember.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        Member byNickname = memberService.findByNickname(currentMember.getUsername());
        return ResponseEntity.ok().body(new ProfileResponse(byNickname));
    }

    // 해당 유저의 프로필 수정
    @PostMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity profileUpdate(@CurrentMember UserPrincipal currentMember,
                                        @Valid @RequestBody ProfileRequest profileRequest, Errors errors){

        if(currentMember == null) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        if(errors.hasErrors()) {
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

        if(currentMember == null) {
            return new ResponseEntity(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        if(!passwordRequest.getNewPassword().equals(passwordRequest.getNewPasswordConfirm())) {
                return new ResponseEntity(new ApiResponse(false, "입력한 새 패스워드가 일치하지 않습니다."),
                        HttpStatus.BAD_REQUEST);
        }

        if(errors.hasErrors()) {
            return new ResponseEntity(new ApiResponse(false, "길이가 너무 짧거나 깁니다."),
                    HttpStatus.BAD_REQUEST);
        }

        memberService.updatePassword(currentMember.getId(),passwordRequest);

        return ResponseEntity.ok().body(new ApiResponse(true, "패스워드 수정이 완료 되었습니다."));
    }



}
