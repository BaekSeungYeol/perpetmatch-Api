package com.perpetmatch.User.ui;

import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.User.domain.User;
import com.perpetmatch.User.domain.UserRepository;
import com.perpetmatch.api.dto.Profile.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class CheckUserController {


    private final UserRepository userRepository;

    @GetMapping("/user/me")
    public UserDto getCurrentUser(@CurrentMember UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).get();

        return new UserDto(user.getId(), user.getNickname(), user.getEmail());
    }
}
