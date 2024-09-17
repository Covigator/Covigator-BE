package com.ku.covigator.domain.member;

import com.ku.covigator.domain.BaseTime;
import com.ku.covigator.domain.Dibs;
import com.ku.covigator.domain.travelstyle.TravelStyle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(name = "platform")
    @Enumerated(value = EnumType.STRING)
    private Platform platform;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_style_id", referencedColumnName = "id")
    private TravelStyle travelStyle;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<Dibs> dibs = new ArrayList<>();

    @Builder
    public Member(String nickname, String email, String password, String imageUrl, Platform platform) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.status = Status.ACTIVE;
        this.platform = platform;
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void putTravelStyle(TravelStyle travelStyle) {
        this.travelStyle = travelStyle;
    }

    public void updateTravelStyle(TravelStyle newTravelStyle) {
        this.travelStyle.patchTravelStyle(newTravelStyle);
    }
}
