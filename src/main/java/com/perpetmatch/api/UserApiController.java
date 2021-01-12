package com.perpetmatch.api;

import com.perpetmatch.Domain.User;
import com.perpetmatch.modules.Member.UserRepository;
import com.perpetmatch.modules.Member.UserService;
import com.perpetmatch.jjwt.JwtTokenProvider;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.jjwt.resource.JwtAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;


    // 이메일 확인 localhost:3000/check-email-token 페이지-> then -> localhost:8080/check-success -> 밑에 logic
    @GetMapping("/check-email-token")
    public ResponseEntity checkEmailToken(String token, String email) {

        boolean checkEmail = userService.verifyingEmail(token, email);

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
        User member = userRepository.findByEmail(email);
        if(member == null) {
            return new ResponseEntity(new ApiResponse(false, "유효한 이메일 주소가 아닙니다."),
                    HttpStatus.BAD_REQUEST);
        }

        userService.sendLoginLink(member);

        return ResponseEntity.ok().body(new ApiResponse(true, "이메일 로그인이 완료 되었습니다."));
    }

    @GetMapping("/login-by-email")
    public ResponseEntity loginByEmail(String token, String email) {
        User member = userRepository.findByEmail(email);

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

