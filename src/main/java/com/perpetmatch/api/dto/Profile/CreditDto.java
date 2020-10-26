package com.perpetmatch.api.dto.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
