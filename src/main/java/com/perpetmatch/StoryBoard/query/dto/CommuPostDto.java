package com.perpetmatch.StoryBoard.query.dto;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommuPostDto {


    boolean checked;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;

    private int likes;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String description;
}
