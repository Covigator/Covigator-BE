package com.ku.covigator.controller;

import com.ku.covigator.dto.request.PostCourseRequest;
import com.ku.covigator.dto.response.GetCommunityCourseInfoResponse;
import com.ku.covigator.dto.response.GetCommunityCourseListResponse;
import com.ku.covigator.dto.response.GetDibsCourseListResponse;
import com.ku.covigator.dto.response.GetMyCourseListResponse;
import com.ku.covigator.security.jwt.LoggedInMemberId;
import com.ku.covigator.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "course", description = "커뮤니티 코스")
@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "코스 등록")
    @PostMapping("/community/courses")
    public ResponseEntity<Void> addCommunityCourse(@Parameter(hidden = true) @LoggedInMemberId Long memberId,
                                                   @RequestPart(value = "postCourseRequest") @Valid PostCourseRequest request,
                                                   @RequestPart(value = "image", required = false) List<MultipartFile> images) {
        courseService.addCommunityCourse(memberId, request, images);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전체 코스 조회")
    @GetMapping("/community/courses")
    public ResponseEntity<GetCommunityCourseListResponse> getAllCommunityCourses(

            @Parameter(hidden = true) @LoggedInMemberId Long memberId,

            @Parameter(description = "페이지 번호 (0부터 시작)", schema = @Schema(defaultValue = "0"))
            @RequestParam(value = "page", defaultValue = "0") int page,

            @Parameter(description = "페이지 당 항목 수", schema = @Schema(defaultValue = "10"))
            @RequestParam(value = "size", defaultValue = "10") int size,

            @Parameter(description = "정렬 기준 (별점순: avgScore), (좋아요순: DibsCnt)", schema = @Schema(defaultValue = "createdAt"))
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return ResponseEntity.ok(courseService.findAllCourses(pageable, memberId));
    }

    @Operation(summary = "상세 코스 조회")
    @GetMapping("/community/courses/{course_id}")
    public ResponseEntity<GetCommunityCourseInfoResponse> getCommunityCourseInfo(
            @Parameter(hidden = true) @LoggedInMemberId Long memberId,
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
    @GetMapping("/my-page/dibs-courses")
    public ResponseEntity<GetDibsCourseListResponse> getLikedCourses(@Parameter(hidden = true) @LoggedInMemberId Long memberId){
        return ResponseEntity.ok(courseService.findLikedCourses(memberId));
    }

    @Operation(summary = "마이 코스 모아보기")
    @GetMapping("/my-page/my-courses")
    public ResponseEntity<GetMyCourseListResponse> getMyCourses(@Parameter(hidden = true) @LoggedInMemberId Long memberId) {
        return ResponseEntity.ok(courseService.findMyCourses(memberId));
    }
}
