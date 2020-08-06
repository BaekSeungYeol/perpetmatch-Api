package com.perpetmatch.api.dto.Board;


import com.perpetmatch.Board.Gender;
import com.perpetmatch.Domain.Board;
import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.PetAge;
import com.perpetmatch.Domain.Zone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardGetResponseV1 {
    private Long id;
    private String manager;
    private String title;
    private int credit;
    private Zone zone;
    private String gender;
    private int year;
    private int month;
    private Pet petTitle;
    private PetAge petAge;
    private String checkUp;
    private String lineAgeImage;
    private String neuteredImage;
    private String description;
    private String boardImage1;
    private String boardImage2;
    private String boardImage3;

    public BoardGetResponseV1(Board board) {
        id = board.getId();
        manager = board.getManager().getNickname();
        title = board.getTitle();
        credit = board.getCredit();
        zone = board.getZone();
        gender = board.getGender().toString();
        year = board.getYear();
        month = board.getMonth();
        petTitle = board.getPetTitle();
        petAge = board.getPetAge();
        checkUp = board.getCheckUp();
        lineAgeImage = board.getLineAgeImage();
        neuteredImage = board.getNeuteredImage();
        description = board.getDescription();
        boardImage1 = board.getBoardImage1();
        boardImage2 = board.getBoardImage2();
        boardImage3 = board.getBoardImage3();
    }

}
