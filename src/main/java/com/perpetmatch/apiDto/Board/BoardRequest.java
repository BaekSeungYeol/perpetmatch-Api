package com.perpetmatch.apiDto.Board;

import com.perpetmatch.Domain.Member;
import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.PetAge;
import com.perpetmatch.Domain.Zone;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(of = "id")
@Getter @Setter
@NoArgsConstructor
public class BoardRequest {

    @NotBlank
    @Length(max = 50)
    private String title;

    @Min(value = 30000)
    @Max(value = 150000)
    private int credit;

    private String zone;
    private String gender;
    private int year;
    private int month;
    private String petTitle;
    private String checkUp;
    private String lineAgeImage;
    private String neuteredImage;
    private String description;
    private String boardImage1;
    private String boardImage2;
    private String boardImage3;
}
