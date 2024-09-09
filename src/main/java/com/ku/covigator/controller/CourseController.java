package com.ku.covigator.controller;

import com.ku.covigator.dto.request.PostCourseRequest;
import com.ku.covigator.dto.response.GetCommunityCourseInfoResponse;
import com.ku.covigator.dto.response.GetCourseListResponse;
import com.ku.covigator.security.jwt.LoggedInMemberId;
import com.ku.covigator.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "course", description = "커뮤니티 코스")
@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "코스 등록")
    @PostMapping("/community/courses")
    public ResponseEntity<Void> addCommunityCourse(@LoggedInMemberId Long memberId, @RequestBody PostCourseRequest request) {
        courseService.addCommunityCourse(memberId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전체 코스 조회")
    @GetMapping("/community/courses")
    public ResponseEntity<GetCourseListResponse> getAllCommunityCourses(
            @PageableDefault(
                    page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ResponseEntity.ok(courseService.findAllCourses(pageable));
    }

    @Operation(summary = "상세 코스 조회")
    @GetMapping("/community/courses/{course_id}")
    public ResponseEntity<GetCommunityCourseInfoResponse> getCommunityCourseInfo(
            @LoggedInMemberId Long memberId,
            @PathVariable(name = "course_id") Long courseId) {
        return ResponseEntity.ok(courseService.findCourse(memberId, courseId));
    }

    @Operation(summary = "코스 삭제")
    @DeleteMapping("/community/courses/{course_id}")
    public ResponseEntity<Void> deleteCommunityCourse(
            @PathVariable(name = "course_id") Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "찜한 코스 모아보기")
    @GetMapping("/my-page/liked-courses")
    public ResponseEntity<GetCourseListResponse> getLikedCourses(@LoggedInMemberId Long memberId){
        return ResponseEntity.ok(courseService.findLikedCourses(memberId));
    }

    @Operation(summary = "마이 코스 모아보기")
    @GetMapping("/my-page/my-courses")
    public ResponseEntity<GetCourseListResponse> getMyCourses(@LoggedInMemberId Long memberId) {
        return ResponseEntity.ok(courseService.findMyCourses(memberId));
    }
}
