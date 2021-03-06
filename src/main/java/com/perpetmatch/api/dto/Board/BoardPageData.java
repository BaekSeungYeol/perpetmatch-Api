package com.perpetmatch.api.dto.Board;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.AdoptBoard.domain.Board;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
    private int year;
    private int month;
    private String boardImage1;
    private ArrayList<String> tags = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private boolean closed;

    public BoardPageData(Board board) {
        id = board.getId();
        title=board.getTitle();
        credit =board.getCredit();
        boardImage1 = board.getBoardImage1();
        tags.add(board.getZone().getProvince());
        tags.add(board.getPetTitle().getTitle());
        tags.add(board.getPetAge().getPetRange());
        year= board.getYear();
        month=board.getMonth();
        if(board.isHasCheckUp()) tags.add("건강검진증");
        if(board.isHasLineAge()) tags.add("혈통서");
        if(board.isHasNeutered()) tags.add("중성화");
        createdAt = board.getCreatedAt();
        closed = board.isClosed();
    }
}
