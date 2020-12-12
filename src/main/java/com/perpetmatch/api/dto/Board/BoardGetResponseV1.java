package com.perpetmatch.api.dto.Board;


import com.perpetmatch.modules.Board.Gender;
import com.perpetmatch.Domain.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardGetResponseV1 {
    private Long id;
    private String manager;
    private String title;
    private int credit;
    private String zone;
    private String gender;
    private int year;
    private int month;
    private String petTitle;
    private String petAge;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String checkUpImage;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String lineAgeImage;
    private Boolean hasNeutered;
    private String description;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String boardImage1;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String boardImage2;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String boardImage3;
    private boolean closed;

    public BoardGetResponseV1(Board board) {
        id = board.getId();
        manager = board.getManager().getNickname();
        title = board.getTitle();
        credit = board.getCredit();
        zone = board.getZone().getProvince();
        gender = board.getGender().toString();
        year = board.getYear();
        month = board.getMonth();
        petTitle = board.getPetTitle().getTitle();
        petAge = board.getPetAge().getPetRange();
        checkUpImage = board.getCheckUpImage();
        lineAgeImage = board.getLineAgeImage();
        hasNeutered = board.isHasNeutered();
        description = board.getDescription();
        boardImage1 = board.getBoardImage1();
        boardImage2 = board.getBoardImage2();
        boardImage3 = board.getBoardImage3();
        closed = board.isClosed();
    }

}
