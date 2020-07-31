package com.perpetmatch.apiDto.Board;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BoardAllResponse {

    private String title;
    private String description;
    private int credit;
    private String image;
    private boolean neutered;
    private LocalDateTime publishedDateTime;

    public BoardAllResponse(String title, String description, int credit, String image, boolean neutered, LocalDateTime publishedDateTime) {
        this.title = title;
        this.description = description;
        this.credit = credit;
        this.image = image;
        this.neutered = neutered;
        this.publishedDateTime = publishedDateTime;
    }
}

