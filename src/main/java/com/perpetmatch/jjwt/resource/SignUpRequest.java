package com.perpetmatch.jjwt.resource;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;


@Getter
@Setter
@Builder
public class SignUpRequest {

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[r-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;
}
