package com.perpetmatch.api.dto.User;

import com.perpetmatch.Domain.User;
import lombok.*;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCredit {

    private Long id;
    private String nickname;
    private int credit;

    public UserCredit(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.credit = user.getCredit();
    }
}
