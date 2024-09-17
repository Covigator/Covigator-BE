package com.ku.covigator.controller;

import com.ku.covigator.security.jwt.LoggedInMemberId;
import com.ku.covigator.service.DibsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dibs", description = "찜")
@RestController
@RequiredArgsConstructor
public class DibsController {

    private final DibsService dibsService;

    @Operation(summary = "좋아요(찜) 등록")
    @PostMapping("/community/courses/{course_id}/dibs")
    public ResponseEntity<Void> likeCourse(
            @LoggedInMemberId Long memberId,
            @PathVariable(name = "course_id") Long courseId) {
        dibsService.addLike(memberId, courseId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요(찜) 해제")
    @DeleteMapping("/community/courses/{course_id}/dibs")
    public ResponseEntity<Void> deleteCourseLiked(
            @LoggedInMemberId Long memberId,
            @PathVariable(name = "course_id") Long courseId) {
        dibsService.deleteLike(memberId, courseId);
        return ResponseEntity.ok().build();
    }
}
