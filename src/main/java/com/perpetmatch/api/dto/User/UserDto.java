package com.perpetmatch.api.dto.User;

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
