package com.perpetmatch.api.dto.Profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetResponseOne {

    List<String> petTitles;
    List<String> allPetTitles;

}
