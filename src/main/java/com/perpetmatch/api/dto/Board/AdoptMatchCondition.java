package com.perpetmatch.api.dto.Board;

import com.perpetmatch.pet.domain.Pet;
import com.perpetmatch.pet.domain.PetAge;
import com.perpetmatch.Zone.domain.Zone;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class AdoptMatchCondition {


    Set<Zone> zones = new HashSet<>();
    Set<Pet> petTitles = new HashSet<>();
    Set<PetAge> petAges = new HashSet<>();
    boolean wantCheckUp;
    boolean wantLineAge;
    boolean wantNeutered;
    int expectedFeeForMonth;

}
