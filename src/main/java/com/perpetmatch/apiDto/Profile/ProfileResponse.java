package com.perpetmatch.apiDto.Profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.Domain.*;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

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
    private int howManyPets;
    private int expectedFeeForMonth;
    private String location;
    private String profileImage;
    private String description;
    private boolean wantCheckUp;
    private boolean wantLineAge;
    private boolean wantNeutered;

}