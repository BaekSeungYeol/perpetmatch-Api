package com.perpetmatch.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class User {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String password;

    @Column(unique = true)
    private String email;

    private String emailCheckToken;

    private boolean emailVerified = false;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime joinedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Pet> petTitles = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Zone> zones = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<PetAge> petAges = new HashSet<>();

    private int credit;

    private String houseType;

    private String occupation;

    private boolean experience;

    private boolean liveAlone;

    private int howManyPets;

    private int expectedFeeForMonth;

    private String phoneNumber;

    private String location;

    @Lob @Basic(fetch = FetchType.LAZY)
    private String description;

    private boolean wantCheckUp;
    private boolean wantLineAge;
    private boolean wantNeutered;


    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public void completeSignup(String token) {
        this.setEmailVerified(true);
        this.setJoinedAt(LocalDateTime.now());
    }
}
