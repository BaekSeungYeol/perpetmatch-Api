package com.perpetmatch.modules.Member.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perpetmatch.Domain.*;
import com.perpetmatch.Domain.Item.Item;
import com.perpetmatch.jjwt.oauth2.user.AuthProvider;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class User extends DateAudit {

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

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Board> likeList = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Commu> commus = new HashSet<>();

//    @ManyToMany(fetch = FetchType.LAZY)
//    private Set<Board> bookmark = new HashSet<>();

    private int age;

    private int credit;

    private String houseType = "apartment";

    private String occupation = "";

    private boolean experience;

    private boolean liveAlone;

    private boolean hasPet;

    private int expectedFeeForMonth;

    private String phoneNumber;

    private String location = "서울특별시";

    @Lob @Basic(fetch = FetchType.EAGER)
    private String description;

    private boolean wantCheckUp;
    private boolean wantLineAge;
    private boolean wantNeutered;


    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    private boolean profileDone;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;
    private String imageUrl;


    /**
     * 쇼핑하기
     */

    //member field에 의해 맵핑됨 읽기전용
    @JsonIgnore
    @OneToMany
    private Set<Order> orders = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    private Set<OrderItem> bag = new HashSet<>();

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public void completeSignup(String token) {
        this.setEmailVerified(true);
        this.setJoinedAt(LocalDateTime.now());
    }

//    public void calculateCredit(int totalSum) {
//        this.credit = Math.max(0, this.credit-totalSum);
//    }

    public void removeLikeBoard(Board board) {
        this.getLikeList().remove(board);
    }
    public void addLikeBoard(Board board) {
        this.getLikeList().add(board);
    }

    public boolean hasLikeBoard(Board board) {
        return this.getLikeList().contains(board);
    }

    public void calculate() {
        int totalSum = 0;
        for (OrderItem bag : this.bag) {
            totalSum += bag.getTotalPrice();
        }
        this.bag.clear();
        this.credit = Math.max(0, credit - totalSum);
    }

    public void makeCommuBoard(Commu savedCommu) {
        this.commus.add(savedCommu);
    }
}
