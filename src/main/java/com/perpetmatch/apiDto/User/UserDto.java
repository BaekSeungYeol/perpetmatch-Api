package com.perpetmatch.apiDto.User;

import lombok.*;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String name;

    private String email;
}
