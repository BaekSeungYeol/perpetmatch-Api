package com.perpetmatch.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.Board.Gender;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Board {

    @Id
    @GeneratedValue
    private Long id;

    // 관리자
    @OneToOne(fetch = FetchType.LAZY)
    private User manager;

    // 신청자들
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    private String title;

    private int credit;

    @OneToOne(fetch = FetchType.LAZY)
    private Zone zone;

    private Gender gender;

    private int year;
    private int month;

    @OneToOne(fetch = FetchType.LAZY)
    private PetAge petAge;

    // 품종
    @OneToOne(fetch = FetchType.LAZY)
    private Pet pet;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String checkUp;

    private boolean hasCheckUp;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String lineAgeImage;

    private boolean hasLineAgeImage;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String neuteredImage;

    private boolean neutered;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime publishedDateTime;

    private boolean closed;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String description;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String boardImage1;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String boardImage2;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String boardImage3;

    public void addManager(User member) {
        this.manager = member;
    }
}
