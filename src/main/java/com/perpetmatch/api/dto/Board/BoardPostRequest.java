package com.perpetmatch.api.dto.Board;

import com.perpetmatch.Board.Gender;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(of = "id")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPostRequest {

    @NotBlank
    @Length(max = 50)
    private String title;

    @Min(value = 30000)
    @Max(value = 150000)
    private int credit;

    private String zone;
    private Gender gender;
    private int year;

    @Min(value = 1)
    @Max(value = 12)
    private int month;
    private String petTitle;
    private String checkUpImage;
    private String lineAgeImage;
    private String neuteredImage;
    private Boolean hasNeutered;
    private String description;
    private String boardImage1;
    private String boardImage2;
    private String boardImage3;
}
