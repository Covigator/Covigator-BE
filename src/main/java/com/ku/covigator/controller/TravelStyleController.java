package com.ku.covigator.controller;

import com.ku.covigator.dto.request.PatchTravelStyleRequest;
import com.ku.covigator.dto.request.PostTravelStyleRequest;
import com.ku.covigator.security.jwt.LoggedInMemberId;
import com.ku.covigator.service.TravelStyleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Travel Style", description = "여행 스타일")
@RestController
@RequestMapping("/members/travel-styles")
@RequiredArgsConstructor
public class TravelStyleController {

    private final TravelStyleService travelStyleService;

    @Operation(summary = "여행 스타일 저장")
    @PostMapping
    public ResponseEntity<Void> saveTravelStyle(@Parameter(hidden = true) @LoggedInMemberId Long memberId,
                                                @RequestBody PostTravelStyleRequest request) {
        travelStyleService.saveTravelStyle(memberId, request.toEntity());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "여행 스타일 수정")
    @PatchMapping
    public ResponseEntity<Void> patchTravelStyle(@Parameter(hidden = true) @LoggedInMemberId Long memberId,
                                                 @RequestBody PatchTravelStyleRequest request) {
        travelStyleService.updateTravelStyle(memberId, request.toEntity());
        return ResponseEntity.ok().build();
    }

}
