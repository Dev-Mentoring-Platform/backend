package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.LecturePriceWithLectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"MenteeEnrollmentController"})
@RequestMapping("/api/mentees/my-enrollments")
@RestController
@RequiredArgsConstructor
public class MenteeEnrollmentController {

    private final MenteeReviewService menteeReviewService;
    private final EnrollmentService enrollmentService;

    @ApiOperation("수강 강의 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getEnrolledLectures(@CurrentUser User user, @RequestParam(defaultValue = "1") Integer page) {
        Page<LecturePriceWithLectureResponse> lectures = enrollmentService.getLecturePriceWithLectureResponsesOfMentee(user, page);
        return ResponseEntity.ok(lectures);
    }

    @ApiOperation("수강 강의 개별 조회")
    @GetMapping("/{enrollment_id}/lecture")
    public ResponseEntity<?> getEnrolledLecture(@CurrentUser User user, @PathVariable(name = "enrollment_id") Long enrollmentId) {
        LecturePriceWithLectureResponse lecture = enrollmentService.getLecturePriceWithLectureResponseOfMentee(user, enrollmentId);
        return ResponseEntity.ok(lecture);
    }

    @ApiOperation("리뷰 미작성 수강내역 리스트 - 페이징")
    @GetMapping("/unreviewed")
    public ResponseEntity<?> getUnreviewedLecturesOfMentee(@CurrentUser User user,
                                                           @RequestParam(defaultValue = "1") Integer page) {
        Page<EnrollmentWithSimpleLectureResponse> enrollments = enrollmentService.getEnrollmentWithSimpleLectureResponses(user, false, page);
        return ResponseEntity.ok(enrollments);
    }

    @ApiOperation("수강내역 조회")
    @GetMapping("/{enrollment_id}")
    public ResponseEntity<?> getEnrollment(@CurrentUser User user, @PathVariable(name = "enrollment_id") Long enrollmentId) {
        EnrollmentWithSimpleLectureResponse enrollment = enrollmentService.getEnrollmentWithSimpleLectureResponse(user, enrollmentId);
        return ResponseEntity.ok(enrollment);
    }

    @ApiOperation("리뷰 작성")
    @PostMapping("/{enrollment_id}/reviews")
    public ResponseEntity<?> newReview(@CurrentUser User user,
                                       @PathVariable(name = "enrollment_id") Long enrollmentId,
                                       @RequestBody @Valid MenteeReviewCreateRequest menteeReviewCreateRequest) {

        menteeReviewService.createMenteeReview(user, enrollmentId, menteeReviewCreateRequest);
        return created();
    }

}
