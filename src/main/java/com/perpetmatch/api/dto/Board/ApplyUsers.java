package com.perpetmatch.api.dto.Board;


import com.perpetmatch.User.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyUsers {
    private Long id;
    private String nickname;
    private String profileImage;
    private String phoneNumber;

    public ApplyUsers(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.phoneNumber = user.getPhoneNumber();
    }
}
