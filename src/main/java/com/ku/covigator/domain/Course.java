package com.ku.covigator.domain;

import com.ku.covigator.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Course extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_public")
    private Character isPublic;

    @Column(name = "like_cnt")
    @ColumnDefault("0")
    private Long likeCnt;

    @Column(name = "avg_score")
    @ColumnDefault("0")
    private Double avgScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    private List<CoursePlace> places = new ArrayList<>();

    @Builder
    public Course(String name, String description, Character isPublic, Long likeCnt, Double avgScore, Member member) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.likeCnt = likeCnt;
        this.avgScore = avgScore;
        this.member = member;
    }

}
