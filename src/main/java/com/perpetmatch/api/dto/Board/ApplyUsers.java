package com.perpetmatch.api.dto.Board;


import com.perpetmatch.Domain.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyUsers {
    private Long id;
    private String nickname;
    private String profileImage;
    private List<String> tags = new ArrayList<>();

    public ApplyUsers(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        tags.addAll(user.getPetAges().stream().map(PetAge::toString).collect(Collectors.toList()));
        tags.addAll(user.getPetTitles().stream().map(Pet::toString).collect(Collectors.toList()));
        tags.addAll(user.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
    }
}
