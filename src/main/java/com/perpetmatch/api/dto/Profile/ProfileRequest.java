package com.perpetmatch.api.dto.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {


    @NotNull
    private int age;
    @NotBlank
    private String occupation;
    @NotBlank
    private String location;
    @NotBlank
    private String houseType;
    @NotNull
    private boolean experience;
    @NotNull
    private boolean liveAlone;
    @NotNull
    private int howManyPets;
    @NotNull
    private int expectedFeeForMonth;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String description;

    private String profileImage;

    private boolean wantCheckUp;

    private boolean wantLineAge;

    private boolean wantNeutered;


}
