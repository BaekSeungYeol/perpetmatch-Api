package com.perpetmatch.apiDto.Profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetAgeResponseOne {

    List<String> ages;
    List<String> allAges;
}
