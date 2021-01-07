package com.perpetmatch.modules.Member.query.dto;

import lombok.*;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String nickname;

    private String email;
}
