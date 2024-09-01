package com.ku.covigator.controller;

import com.ku.covigator.dto.request.PostCourseRequest;
import com.ku.covigator.security.jwt.LoggedInMemberId;
import com.ku.covigator.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "course", description = "커뮤니티 코스")
@RestController
@RequestMapping("/community/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "커뮤니티 코스 등록")
    @PostMapping
    public ResponseEntity<Void> addCommunityCourse(@LoggedInMemberId Long memberId, @RequestBody PostCourseRequest request) {
        courseService.addCommunityCourse(memberId, request);
        return ResponseEntity.ok().build();
    }
}
