package com.perpetmatch.api.dto.Board;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.Domain.Board;
import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.PetAge;
import com.perpetmatch.Domain.Zone;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPageData {
    private Long id;
    private String title;
    private int credit;
    private String gender;
    private String zone;
    private String petTitle;
    private String petAge;
    private int year;
    private int month;
    private boolean hasCheckup;
    private boolean hasLineAgeImage;
    private boolean neutered;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime publishedDateTime;

    public BoardPageData(Board board) {
        id = board.getId();
        title=board.getTitle();
        credit =board.getCredit();
        gender=board.getGender().toString();
        zone=board.getZone().getProvince();
        petTitle=board.getPetTitle().getTitle();
        petAge=board.getPetAge().getPetRange();
        year= board.getYear();
        month=board.getMonth();
        hasCheckup=board.isHasCheckUp();
        hasLineAgeImage=board.isHasLineAgeImage();
        neutered=board.isNeutered();
        publishedDateTime = board.getPublishedDateTime();
    }
}
