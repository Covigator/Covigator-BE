package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Place;
import lombok.Builder;
import org.locationtech.jts.geom.Geometry;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetPlaceInfoResponse(String name, String category, String address, String floor, String dongName, String buildingName, Double latitude, Double longitude, String imageUrl) {
    public static GetPlaceInfoResponse fromEntity(Place place) {
        return GetPlaceInfoResponse.builder()
                .address(place.getAddress())
                .name(place.getName())
                .buildingName(place.getBuildingName())
                .latitude(place.getCoordinate().getY())
                .longitude(place.getCoordinate().getX())
                .floor(place.getFloor())
                .dongName(place.getDongName())
                .imageUrl(place.getImageUrl())
                .category(place.getCategory())
                .build();
    }
}
