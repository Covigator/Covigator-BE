package com.ku.covigator.service;

import com.ku.covigator.domain.Place;
import com.ku.covigator.exception.notfound.NotFoundPlaceException;
import com.ku.covigator.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class PlaceServiceTest {

    @Autowired
    PlaceRepository placeRepository;
    @Autowired
    PlaceService placeService;

    @BeforeEach
    void tearDown() {
        placeRepository.deleteAllInBatch();
    }

    @DisplayName("장소를 조회한다.")
    @Test
    void getPlace() {
        //given
        String name = "코비식당";
        String address = "서울특별시 광진구";

        Place place = Place.builder()
                .name("코비식당")
                .address("서울특별시 광진구")
                .floor("2")
                .buildingName("건국대학교")
                .category("식당")
                .dongName("")
                .imageUrl("")
                .coordinate(createPoint(1.0, 2.0))
                .build();

        placeRepository.save(place);

        //when
        Place savedPlace = placeService.getPlaceInfo(name, address);

        //then
        assertAll(
                () -> assertThat(savedPlace.getFloor()).isEqualTo("2"),
                () -> assertThat(savedPlace.getBuildingName()).isEqualTo("건국대학교"),
                () -> assertThat(savedPlace.getCategory()).isEqualTo("식당"),
                () -> assertThat(savedPlace.getCoordinate().getX()).isEqualTo(1.0),
                () -> assertThat(savedPlace.getCoordinate().getY()).isEqualTo(2.0)
        );
    }

    @DisplayName("잘못된 장소 조회 시 예외가 발생한다.")
    @Test
    void getPlaceFailsWhenPlaceNotFound() {
        //given
        String name = "코비식당2";
        String address = "서울특별시 광진구";

        Place place = Place.builder()
                .name("코비식당")
                .address("서울특별시 광진구")
                .floor("2")
                .buildingName("건국대학교")
                .category("식당")
                .dongName("")
                .imageUrl("")
                .coordinate(createPoint(1.0, 2.0))
                .build();

        placeRepository.save(place);

        //when //then
        assertThatThrownBy(() -> placeService.getPlaceInfo(name, address))
                .isInstanceOf(NotFoundPlaceException.class);
    }

    private Point createPoint(double x, double y) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }

}