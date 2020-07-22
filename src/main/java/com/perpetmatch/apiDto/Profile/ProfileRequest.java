package com.perpetmatch.apiDto.Profile;

import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.Zone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.time.Instant;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {

    // TODO password,email,nickname 바꾸기

    @NotBlank
    private String houseType;
    @NotBlank
    private String occupation;
    @NotNull
    private boolean experience;

    @NotNull
    private boolean liveAlone;
    @NotNull
    private int howManyPets;
    @NotNull
    private int expectedFeeForMonth;
    @NotBlank
    private String location;
    @NotBlank
    private String phoneNumber;

    private String profileImage;

    private boolean wantCheckUp;

    private boolean wantLineAgeImage;

    private boolean wantNeutered;


}
