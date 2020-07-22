package com.perpetmatch.apiDto.Profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetResponseOne {

    Set<PetDto> pets;
    List<String> allPets;

    public PetResponseOne(Set<PetDto> pet) {
        this.pets = pet;
    }
}
