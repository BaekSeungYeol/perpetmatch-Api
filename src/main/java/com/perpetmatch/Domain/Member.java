package com.perpetmatch.Domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    private LocalDateTime joinedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Pet> pet = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Zone> zone = new HashSet<>();

    private int credit;

    private int houseType;

    private String occupation;

    private boolean experience;

    private boolean liveAlone;

    private int howManyPets;

    private int expectedFeeForMonth;

    private String location;

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
