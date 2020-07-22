package com.perpetmatch.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perpetmatch.Domain.Member;
import com.perpetmatch.Member.MemberRepository;
import com.perpetmatch.Member.MemberService;
import com.perpetmatch.apiDto.*;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.JwtTokenProvider;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.jjwt.resource.JwtAuthenticationResponse;
import com.perpetmatch.jjwt.resource.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;


    // 이메일 확인 localhost:3000/check-email-token 페이지-> then -> localhost:8080/check-success -> 밑에 logic
    @GetMapping("/check-email-token")
    public ResponseEntity checkEmailToken(String token, String email) {

        boolean checkEmail = memberService.verifyingEmail(token, email);

        if(!checkEmail){
            return ResponseEntity.badRequest().build();
        }
        else
            return ResponseEntity.ok().build();
    }



    // TODO 이메일로 로그인 해야 한다면 필요함 테스트 아직 x


    //이메일로 로그인 localhost:3000/email-login -> 클릭 누르면 서버로 옴
    @PostMapping("/email-login")
    public ResponseEntity sendEmailLoginLink(String email) {
        Member member = memberRepository.findByEmail(email);
        if(member == null) {
            return new ResponseEntity(new ApiResponse(false, "유효한 이메일 주소가 아닙니다."),
                    HttpStatus.BAD_REQUEST);
        }

        memberService.sendLoginLink(member);

        return ResponseEntity.ok().body(new ApiResponse(true, "이메일 로그인이 완료 되었습니다."));
    }

    @GetMapping("/login-by-email")
    public ResponseEntity loginByEmail(String token, String email) {
        Member member = memberRepository.findByEmail(email);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        member.getEmail(),
                        member.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}

