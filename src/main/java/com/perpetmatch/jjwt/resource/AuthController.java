package com.perpetmatch.jjwt.resource;

import com.perpetmatch.Domain.Member;
import com.perpetmatch.Member.MemberRepository;
import com.perpetmatch.Member.MemberService;
import com.perpetmatch.Role.RoleRepository;
import com.perpetmatch.jjwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final MemberRepository memberRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    private final MemberService memberService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateMember(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerMember(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(memberRepository.existsByNickname(signUpRequest.getNickname())) {
            return new ResponseEntity(new ApiResponse(false, "해당 이름이 이미 존재합니다."),
                    HttpStatus.BAD_REQUEST);
        }

        if(memberRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "이메일이 이미 존재합니다."),
                    HttpStatus.BAD_REQUEST);
        }

        Member result = memberService.join(signUpRequest);


        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/members/{username}")
                .buildAndExpand(result.getNickname()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "회원가입이 성공적으로 완료되었습니다."));
    }
}
