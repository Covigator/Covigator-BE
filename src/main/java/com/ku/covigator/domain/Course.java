package com.ku.covigator.domain;

import com.ku.covigator.domain.member.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    private List<Like> likes = new ArrayList<>();

    @Builder
    public Course(String name, String description, Character isPublic, Member member) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.member = member;
    }

    public double calculateAvgScore() {
        return reviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0.0);
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void addLikes(Like like) {
        this.likes.add(like);
    }

}
