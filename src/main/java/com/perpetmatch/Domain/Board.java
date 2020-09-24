package com.perpetmatch.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.Board.Gender;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Board extends DateAudit{

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

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private int year;
    private int month;

    @OneToOne(fetch = FetchType.LAZY)
    private PetAge petAge;

    // 품종
    @OneToOne(fetch = FetchType.LAZY)
    private Pet petTitle;


    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String checkUpImage;

    private boolean hasCheckUp;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String lineAgeImage;

    private boolean hasLineAge;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String neuteredImage;

    private boolean hasNeutered;

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

    public boolean isMember(String username) {
        for(User user : users) {
            if(user.getNickname().equals(username))
                return true;
        }
        return false;
    }

    public boolean isManager(String username) {
        if(manager.getNickname().equals(username))
            return true;
        else return false;
    }
    public void addMember(User user) {
        this.getUsers().add(user);
    }
    public void removeMember(User user) {
        this.getUsers().remove(user);
    }
}
