package com.perpetmatch.api.dto.Board;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptMatchDto {

    List<String> zones;
    List<String> petTitles;
    List<String> petAges;
    boolean wantCheckUp;
    boolean wantLineAge;
    boolean wantNeutered;
    int expectedFeeForMonth;

}
