package com.perpetmatch.apiDto;

import com.perpetmatch.Domain.Member;
import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.Role;
import com.perpetmatch.Domain.Zone;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private String nickname;
    private String email;
    private Instant joinedAt;
    private int credit;
    private String houseType;
    private String occupation;
    private boolean experience;
    private boolean liveAlone;
    private int howManyPets;
    private int expectedFeeForMonth;
    private String location;
    private String profileImage;

    public ProfileResponse(Member member) {
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.joinedAt = member.getJoinedAt();
        this.credit = member.getCredit();
        this.houseType = member.getHouseType();
        this.occupation = member.getOccupation();
        this.experience = member.isExperience();
        this.liveAlone = member.isLiveAlone();
        this.howManyPets = member.getHowManyPets();
        this.expectedFeeForMonth = member.getExpectedFeeForMonth();
        this.location = member.getLocation();
        this.profileImage = member.getProfileImage();
    }
}