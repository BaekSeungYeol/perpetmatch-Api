package com.perpetmatch.api;

import com.perpetmatch.Domain.Member;
import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.Zone;
import com.perpetmatch.Member.MemberRepository;
import com.perpetmatch.Member.MemberService;
import com.perpetmatch.apiDto.ProfileRequest;
import com.perpetmatch.apiDto.ProfileResponse;
import com.perpetmatch.exception.BadRequestException;
import com.perpetmatch.exception.ResourceNotFoundException;
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
import java.net.URI;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class ProfileApiController {

    private final PetService petService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;


    // 이름으로 유저 한명의 프로필 조회

    @GetMapping("/api/profiles/{nickname}")
    public ResponseEntity<ProfileResponse> profileAll(@PathVariable String nickname) {

        if(!memberRepository.existsByNickname(nickname)) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }
        Member byNickname = memberService.findByNickname(nickname);
        return ResponseEntity.ok().body(new ProfileResponse(byNickname));
    }

    // 해당 유저의 프로필 수정
    @PostMapping("/api/profiles")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity profileUpdate(@CurrentMember UserPrincipal currentMember,
                                        @Valid @RequestBody ProfileRequest profileRequest, Errors errors){

        if(errors.hasErrors()) {
            return new ResponseEntity(new ApiResponse(false, "입력방식이 잘못되었습니다."),
                    HttpStatus.BAD_REQUEST);
        }
        memberService.updateProfile(currentMember.getId(), profileRequest);

        return ResponseEntity.ok().body(new ApiResponse(true, "Profile updated successfully"));
    }

    // 유저 한명의


}
