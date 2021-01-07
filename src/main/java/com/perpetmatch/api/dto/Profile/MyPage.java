package com.perpetmatch.api.dto.Profile;

import com.perpetmatch.modules.Member.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class MyPage {

    private Set<String> tags = new HashSet<>();

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;
    private String nickname;
    private String description;
    private int credit;

    public MyPage(User user) {
        this.profileImage = user.getProfileImage();
        this.nickname = user.getNickname();
        this.description = user.getDescription();
        this.tags.add(user.getAge() + "살");
        if(user.getOccupation() != null) this.tags.add(user.getOccupation());
        if(user.getHouseType() != null && user.getHouseType().equals("apartment")) this.tags.add("아파트");
        if(user.getHouseType() != null && user.getHouseType().equals("house")) this.tags.add("주택");
        if(user.getHouseType() != null && user.getHouseType().equals("etc")) this.tags.add("비주택");
        if(user.getLocation() != null) this.tags.add(user.getLocation());
        if(user.isLiveAlone()) this.tags.add("1인가구");
        if(user.isExperience()) this.tags.add("경험");
        if(user.isHasPet()) this.tags.add("보호자");
        this.credit = user.getCredit();
    }

}
