package com.perpetmatch.Comment.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Comment {

    @Id @GeneratedValue
    private Long id;

    private String nickname;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private String text;
}
