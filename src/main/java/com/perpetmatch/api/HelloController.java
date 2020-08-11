package com.perpetmatch.api;

import com.perpetmatch.Domain.User;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.api.dto.User.UserDto;
import com.perpetmatch.exception.ResourceNotFoundException;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class HelloController {


    private final UserRepository userRepository;

    @GetMapping("/hello-world/{name}")
    public HelloWorldBean helloWorldPathVariable(@PathVariable String name) {
        return new HelloWorldBean(String.format("Hello World, %s", name));
    }

    static class HelloWorldBean {
        private String message;

        public HelloWorldBean(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return String.format("HelloWorldBean [message=%s]", message);
        }
    }

    @GetMapping("/user/me")
    public UserDto getCurrentUser(@CurrentMember UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).get();

        return new UserDto(user.getId(), user.getNickname(), user.getEmail());
    }
}
