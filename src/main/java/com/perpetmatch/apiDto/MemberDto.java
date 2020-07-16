package com.perpetmatch.apiDto;


import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.Zone;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private String nickname;
    private String password;
    private List<Pet> pet = new ArrayList<>();
    private List<Zone> zone = new ArrayList<>();
    private String occupation;
    private boolean experience;
    private boolean liveAlone;
    private int howManyPets;
    private int expectedFeeForMonth;
    private LocalDateTime joinedAt;

    public MemberDto(String nickname) {
        this.nickname = nickname;
    }

}
