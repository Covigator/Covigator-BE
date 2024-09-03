//package com.ku.covigator.domain;
//
//import com.ku.covigator.domain.member.Member;
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class Review extends BaseTime{
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;
//
//    @Column(name = "comment")
//    private String comment;
//
//    @Column(name = "score")
//    private Integer score;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id", referencedColumnName = "id")
//    private Member member;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "course_id", referencedColumnName = "id")
//    private Course course;
//
//    @Builder
//    public Review(String comment, Integer score, Member member, Course course) {
//        this.comment = comment;
//        this.score = score;
//        this.member = member;
//        this.course = course;
//    }
//}
