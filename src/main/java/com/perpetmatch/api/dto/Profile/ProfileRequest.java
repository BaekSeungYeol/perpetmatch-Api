package com.perpetmatch.api.dto.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
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
    private boolean hasPet;
    @NotNull
    private int expectedFeeForMonth;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String description;


    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean wantCheckUp;

    private boolean wantLineAge;

    private boolean wantNeutered;

    private List<String> zones = new ArrayList<>();
    private List<String> petTitles = new ArrayList<>();
    private List<String> petAges = new ArrayList<>();


}
