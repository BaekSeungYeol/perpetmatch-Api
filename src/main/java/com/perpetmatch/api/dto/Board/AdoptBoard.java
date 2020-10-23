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

@Data
@Builder
@NoArgsConstructor
public class AdoptBoard {
    private Long id;
    private String title;
    private int credit;
    private String zone;
    private int year;
    private int month;
    private String petTitle;
    private String petAge;
    private boolean hasCheckUp;
    private boolean hasLineAge;
    private boolean hasNeutered;
    private String description;
    private String boardImage1;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private boolean closed;

    @QueryProjection
    public AdoptBoard(Long id, String title, int credit, String zone, int year, int month, String petTitle, String petAge, boolean hasCheckUp, boolean hasLineAgeImage, boolean neutered, String description, String boardImage1, LocalDateTime createdAt, boolean closed) {
        this.id = id;
        this.title = title;
        this.credit = credit;
        this.zone = zone;
        this.year = year;
        this.month = month;
        this.petTitle = petTitle;
        this.petAge = petAge;
        this.hasCheckUp = hasCheckUp;
        this.hasLineAge = hasLineAgeImage;
        this.hasNeutered = neutered;
        this.description = description;
        this.boardImage1 = boardImage1;
        this.createdAt = createdAt;
        this.closed = closed;
    }
}
