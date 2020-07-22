package com.perpetmatch.apiDto.Profile;

import com.perpetmatch.Domain.Pet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class PetResponseOne {

    Set<Pet> pets;

    public PetResponseOne(Set<Pet> pet) {
        this.pets = pet;
    }
}
