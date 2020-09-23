package com.perpetmatch.api.dto.Profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.Domain.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private Long id;
    private int age;
    private String nickname;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime joinedAt;
    private Set<Pet> petTitles;
    private Set<Zone> zones;
    private Set<PetAge> petAges;
    private int credit;
    private String houseType;
    private String occupation;
    private boolean experience;
    private boolean liveAlone;
    private String phoneNumber;
    private boolean hasPet;
    private int expectedFeeForMonth;
    private String location;
    private String profileImage;
    private String description;
    private boolean wantCheckUp;
    private boolean wantLineAge;
    private boolean wantNeutered;

}