package com.perpetmatch.modules.Member;

import com.perpetmatch.Domain.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
public class UserMember extends org.springframework.security.core.userdetails.User {

    private User member;

    public UserMember(User member) {
        super(member.getNickname(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.member = member;
    }
}
