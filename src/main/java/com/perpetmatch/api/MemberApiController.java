package com.perpetmatch.api;

import com.perpetmatch.Domain.Member;
import com.perpetmatch.Member.MemberRepository;
import com.perpetmatch.Member.MemberService;
import com.perpetmatch.apiDto.*;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    //수정
    @PutMapping("/api/members/{id}")
    @PreAuthorize("hasRole('USER')")
    public UpdateMemberResponse updatemember(@PathVariable Long id, @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request);
        Member findmember = memberService.findOne(id);
        return new UpdateMemberResponse(findmember.getId());
    }

    //조회
    @GetMapping("/api/members")
    public AllMembersResponse getAllmembers(@CurrentMember UserPrincipal currentMember) {

        Long id = currentMember.getId();
        List<Member> members = memberService.findMembers();

        List<MemberDto> collect = members.stream().map(m -> new MemberDto(m.getNickname())).collect(Collectors.toList());
        return new AllMembersResponse(collect);
    }

    //삭제


    // 이메일 확인 localhost:3000/check-email-token -> then -> localhost:8080/check-success -> 밑에 logic
    @GetMapping("/check-email-token")
    public ResponseEntity checkEmailToken(String token, String email) {

        boolean checkEmail = memberService.verifyingEmail(token, email);

        if(!checkEmail){
            return ResponseEntity.badRequest().build();
        }
        else
            return ResponseEntity.ok().build();
    }
}

