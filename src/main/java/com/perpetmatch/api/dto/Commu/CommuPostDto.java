package com.perpetmatch.api.dto.Commu;

import com.perpetmatch.Board.Gender;
import com.perpetmatch.Domain.Comment;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

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
