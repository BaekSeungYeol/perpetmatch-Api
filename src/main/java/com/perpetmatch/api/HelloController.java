package com.perpetmatch.api;

import com.perpetmatch.Domain.User;
import com.perpetmatch.modules.Member.UserRepository;
import com.perpetmatch.api.dto.User.UserDto;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class HelloController {


    private final UserRepository userRepository;

    @GetMapping("/user/me")
    public UserDto getCurrentUser(@CurrentMember UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).get();

        return new UserDto(user.getId(), user.getNickname(), user.getEmail());
    }
}
