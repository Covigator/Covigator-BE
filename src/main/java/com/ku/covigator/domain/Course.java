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

    @Column(name = "dibs_cnt")
    @ColumnDefault("0")
    private Long dibsCnt;

    @Column(name = "avg_score")
    @ColumnDefault("0")
    private Double avgScore;

    @Column(name = "review_cnt")
    @ColumnDefault("0")
    private Long reviewCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    private List<CoursePlace> places = new ArrayList<>();

    @Builder
    public Course(String name, String description, Character isPublic, Long dibsCnt, Double avgScore, Long reviewCnt, Member member) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.dibsCnt = dibsCnt;
        this.avgScore = avgScore;
        this.reviewCnt = reviewCnt;
        this.member = member;
    }

    public void updateAvgScore(Integer score) {
        this.reviewCnt += 1;
        this.avgScore = ((this.avgScore * (this.reviewCnt - 1)) + score) / this.reviewCnt;
    }

    public void increaseDibsCnt() {
        this.dibsCnt += 1;
    }

    public void decreaseDibsCnt() {
        this.dibsCnt -= 1;
    }

    public String getThumbnailImage() {

        if(this.places.isEmpty()) return null;
        return this.places.get(0).getImageUrl();
    }

    public void addPlace(CoursePlace place) {
        this.places.add(place);
    }

}
