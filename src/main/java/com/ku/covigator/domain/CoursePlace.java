package com.ku.covigator.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoursePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "category")
    private String category;

    @Column(name = "address")
    private String address;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;

    @Builder
    public CoursePlace(String name, String description, String category, String address, Course course, String imageUrl) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.address = address;
        this.course = course;
        this.imageUrl = imageUrl;
    }

    public void addImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
