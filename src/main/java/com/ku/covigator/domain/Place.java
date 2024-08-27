package com.ku.covigator.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "address")
    private String address;

    @Column(name = "floor")
    private String floor;

    @Column(name = "dong_name")
    private String dongName;

    @Column(name = "building_name")
    private String buildingName;

    @Column(name = "coordinate")
    private Point coordinate;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder
    public Place(String name, String category, String address, String floor, String dongName, String buildingName, Point coordinate, String imageUrl) {
        this.name = name;
        this.category = category;
        this.address = address;
        this.floor = floor;
        this.dongName = dongName;
        this.buildingName = buildingName;
        this.coordinate = coordinate;
        this.imageUrl = imageUrl;
    }
}
