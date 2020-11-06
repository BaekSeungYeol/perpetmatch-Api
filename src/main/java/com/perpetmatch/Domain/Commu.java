package com.perpetmatch.Domain;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Commu extends DateAudit{

    @Id @GeneratedValue
    private Long id;

    boolean checked;

    private String nickname;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;

    private int likes;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String description;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();


}
