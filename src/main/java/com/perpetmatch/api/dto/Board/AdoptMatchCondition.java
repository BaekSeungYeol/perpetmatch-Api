package com.perpetmatch.api.dto.Board;

import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.PetAge;
import com.perpetmatch.Domain.Zone;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AdoptMatchCondition {

    //
    Set<Zone> zones;
    Set<Pet> petTitles;
    Set<PetAge> petAges;
    boolean wantCheckUp;
    boolean wantLineAge;
    boolean wantNeutered;
    int credit;

}
