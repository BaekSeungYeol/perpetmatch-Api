package com.perpetmatch.api.dto.Profile;

import lombok.*;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String nickname;

    private String email;
}
