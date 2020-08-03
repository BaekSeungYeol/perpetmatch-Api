package com.perpetmatch.apiDto.Profile;

import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.PetAge;
import com.perpetmatch.Domain.Zone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {

    private Long id;
    private String nickname;
    private String email;
}
