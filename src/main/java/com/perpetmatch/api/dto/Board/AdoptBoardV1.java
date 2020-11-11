package com.perpetmatch.api.dto.Board;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.Board.Gender;
import com.perpetmatch.Domain.Board;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptBoardV1 {
    private Long id;
    private String title;
    private int credit;
    private int year;
    private int month;
    private ArrayList<String> tags = new ArrayList<>();
    private String boardImage1;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private boolean hasCheckUp;
    private boolean hasLineAge;
    private boolean closed;


    public AdoptBoardV1(AdoptBoard board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.credit = board.getCredit();
        this.year = board.getYear();
        this.month = board.getMonth();
        tags.add(board.getZone());
        tags.add(board.getPetTitle());
        tags.add(board.getPetAge());
        if(board.isHasCheckUp()) tags.add("건강검진증");
        if(board.isHasLineAge()) tags.add("혈통서");
        if(board.isHasNeutered()) tags.add("중성화");
        this.boardImage1 = board.getBoardImage1();
        this.createdAt = board.getCreatedAt();
        this.hasCheckUp = board.isHasCheckUp();
        this.hasLineAge = board.isHasLineAge();
        this.closed = board.isClosed();
    }
}
