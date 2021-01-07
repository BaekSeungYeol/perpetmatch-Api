package com.perpetmatch.api.dto.Profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.Domain.*;
import com.perpetmatch.modules.Member.domain.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProfileResponse {

    private Long id;
    private int age;
    private String nickname;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime joinedAt;
    private Set<String> petTitles;
    private Set<String> zones;
    private Set<String> petAges;
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

    public ProfileResponse(User user) {
        this.id = user.getId();
        this.age = user.getAge();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.joinedAt = user.getJoinedAt();
        this.petTitles = user.getPetTitles().stream().map(Pet::toString).collect(Collectors.toSet());
        this.zones = user.getZones().stream().map(Zone::toString).collect(Collectors.toSet());
        this.petAges = user.getPetAges().stream().map(PetAge::toString).collect(Collectors.toSet());
        this.credit = user.getCredit();
        this.houseType = user.getHouseType();
        this.occupation = user.getOccupation();
        this.experience = user.isExperience();
        this.liveAlone = user.isLiveAlone();
        this.phoneNumber = user.getPhoneNumber();
        this.hasPet = user.isHasPet();
        this.expectedFeeForMonth = user.getExpectedFeeForMonth();
        this.location = user.getLocation();
        this.profileImage = user.getProfileImage();
        this.description = user.getDescription();
        this.wantCheckUp = user.isWantCheckUp();
        this.wantLineAge = user.isWantLineAge();
        this.wantNeutered = user.isWantNeutered();
    }
}