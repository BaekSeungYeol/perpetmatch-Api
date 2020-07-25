package com.perpetmatch.Domain;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of="id")
public class Board {

    @Id
    @GeneratedValue
    private Long id;

    String manager;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Member> members = new HashSet<>();

    private String title;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String description;

    private int credit;

    private String gender;

    private int age;

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
    private String image;

    private boolean neutered;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime publishedDateTime;


    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Pet> pet = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Zone> zone = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<PetAge> petAge = new HashSet<>();

}
