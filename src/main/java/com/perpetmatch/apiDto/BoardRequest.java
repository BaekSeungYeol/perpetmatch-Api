package com.perpetmatch.apiDto;

import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(of = "id")
@Getter @Setter
@NoArgsConstructor
public class BoardRequest {


    private String title;
    private String description;
    private int credit;
    private String image;
    private boolean neutered;
    private LocalDateTime publishedDateTime;

    public BoardRequest(String title, String description, int credit, String image, boolean neutered, LocalDateTime publishedDateTime) {
        this.title = title;
        this.description = description;
        this.credit = credit;
        this.image = image;
        this.neutered = neutered;
        this.publishedDateTime = publishedDateTime;
    }
}
