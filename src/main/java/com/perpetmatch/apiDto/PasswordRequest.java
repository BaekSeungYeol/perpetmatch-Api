package com.perpetmatch.apiDto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordRequest {

    @Length(min = 8, max = 50)
    private String newPassword;

    @Length(min = 8, max = 50)
    private String newPasswordConfirm;
}
