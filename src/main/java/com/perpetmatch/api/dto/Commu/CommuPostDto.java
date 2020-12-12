package com.perpetmatch.api.dto.Commu;

import com.perpetmatch.modules.Board.Gender;
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
