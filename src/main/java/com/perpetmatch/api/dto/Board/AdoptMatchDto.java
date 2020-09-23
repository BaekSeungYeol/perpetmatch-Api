package com.perpetmatch.api.dto.Board;


import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.PetAge;
import com.perpetmatch.Domain.Zone;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AdoptMatchDto {

    List<String> zones;
    List<String> petTitles;
    List<String> petAges;
    boolean wantCheckUp;
    boolean wantLineAge;
    boolean wantNeutered;
    int credit;

}
