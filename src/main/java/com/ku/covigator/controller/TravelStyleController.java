package com.ku.covigator.controller;

import com.ku.covigator.dto.request.PatchTravelStyleRequest;
import com.ku.covigator.dto.request.PostTravelStyleRequest;
import com.ku.covigator.security.jwt.LoggedInMemberId;
import com.ku.covigator.service.TravelStyleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/travel-styles")
@RequiredArgsConstructor
public class TravelStyleController {

    private final TravelStyleService travelStyleService;

    @PostMapping
    public ResponseEntity<Void> saveTravelStyle(@LoggedInMemberId Long memberId,
                                                @RequestBody PostTravelStyleRequest request) {
        travelStyleService.saveTravelStyle(memberId, request.toEntity());
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> patchTravelStyle(@LoggedInMemberId Long memberId,
                                                 @RequestBody PatchTravelStyleRequest request) {
        travelStyleService.updateTravelStyle(memberId, request.toEntity());
        return ResponseEntity.ok().build();
    }

}
