package com.perpetmatch.Domain;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity @Builder
@EqualsAndHashCode(of="id")
public class Member extends DateAudit {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String password;

    @Column(unique = true)
    private String email;

    private String emailCheckToken;

    private boolean emailVerified = false;

    private Instant joinedAt;

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
        this.setJoinedAt(Instant.now());
    }
}
