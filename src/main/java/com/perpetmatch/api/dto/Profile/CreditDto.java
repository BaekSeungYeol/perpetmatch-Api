package com.perpetmatch.api.dto.Profile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreditDto {

    int credit;

    public CreditDto(int credit) {
        this.credit = credit;
    }
}
