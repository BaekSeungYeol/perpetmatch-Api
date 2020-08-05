package com.perpetmatch.api.dto.Profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetDto {

    private String title; // 품종

    @Override
    public String toString() {
        return title;
    }
}
