package com.perpetmatch.api.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@EqualsAndHashCode(of = "id")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinMemberRequest {

    @NotEmpty
    @NotNull
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[r-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @NotEmpty
    @NotNull
    @Length(min = 8, max = 50)
    private String password;

    @NotEmpty
    @NotNull
    @Email
    private String email;
}
