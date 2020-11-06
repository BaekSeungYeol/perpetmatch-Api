package com.perpetmatch.api.dto.Commu;

import com.perpetmatch.Domain.Comment;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDetailsDto {
    private Long id;

    private String nickname;

    private String profileImage;

    private String text;

    public CommentDetailsDto(Comment comment) {
        this.id = comment.getId();
        this.nickname = comment.getNickname();
        this.profileImage = comment.getProfileImage();
        this.text = comment.getText();
    }
}
