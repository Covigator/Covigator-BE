package com.ku.covigator.controller;

import com.ku.covigator.domain.Place;
import com.ku.covigator.dto.response.GetPlaceInfoResponse;
import com.ku.covigator.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Place", description = "장소")
@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @Operation(summary = "장소 세부 정보 조회")
    @GetMapping
    public ResponseEntity<GetPlaceInfoResponse> getPlaceInfo(@RequestParam String name, @RequestParam String address) {
        Place place = placeService.getPlaceInfo(name, address);
        return ResponseEntity.ok(GetPlaceInfoResponse.fromEntity(place));
    }

}
