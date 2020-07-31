package com.perpetmatch.Domain;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of="id")
public class Board {

    @Id
    @GeneratedValue
    private Long id;

    // 관리자
    @OneToOne(fetch = FetchType.LAZY)
    private Member manager;

    // 신청자들
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Member> members = new HashSet<>();

    private String title;

    private int credit;

    @OneToOne(fetch = FetchType.LAZY)
    private Zone zone;

    private String gender;

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

    private Instant publishedDateTime;

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

    public void addManager(Member member) {
        this.manager = member;
    }
}
