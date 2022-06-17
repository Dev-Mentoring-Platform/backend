package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.LecturePriceWithLectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithLecturePriceResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.created;

@Api(tags = {"MenteeEnrollmentController"})
@RequestMapping("/api/mentees/my-enrollments")
@RestController
@RequiredArgsConstructor
public class MenteeEnrollmentController {

    private final MenteeReviewService menteeReviewService;
    private final EnrollmentService enrollmentService;

/*
    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("수강 강의 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getEnrolledLectures(@CurrentUser User user, @RequestParam(defaultValue = "1") Integer page) {
        Page<LecturePriceWithLectureResponse> lectures = enrollmentService.getLecturePriceWithLectureResponsesOfMentee(user, page);
        return ResponseEntity.ok(lectures);
    }*/
    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("신청 강의 리스트 / 승인 예정 - 페이징")
    @GetMapping("/unchecked")
    public ResponseEntity<?> getUncheckedLectures(@CurrentUser User user, @RequestParam(defaultValue = "1") Integer page) {
        Page<EnrollmentWithLecturePriceResponse> lectures = enrollmentService.getEnrollmentWithLecturePriceResponsesOfMentee(user, false, page);
        return ResponseEntity.ok(lectures);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("신청 강의 리스트 / 승인 완료 - 페이징")
    @GetMapping("/checked")
    public ResponseEntity<?> getCheckedLectures(@CurrentUser User user, @RequestParam(defaultValue = "1") Integer page) {
        Page<EnrollmentWithLecturePriceResponse> lectures = enrollmentService.getEnrollmentWithLecturePriceResponsesOfMentee(user, true, page);
        return ResponseEntity.ok(lectures);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("수강 강의 개별 조회")
    @GetMapping("/{enrollment_id}/lecture")
    public ResponseEntity<?> getEnrolledLecture(@CurrentUser User user, @PathVariable(name = "enrollment_id") Long enrollmentId) {
        LecturePriceWithLectureResponse lecture = enrollmentService.getLecturePriceWithLectureResponseOfMentee(user, enrollmentId);
        return ResponseEntity.ok(lecture);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("리뷰 미작성 수강내역 리스트 - 페이징")
    @GetMapping("/unreviewed")
    public ResponseEntity<?> getUnreviewedLecturesOfMentee(@CurrentUser User user,
                                                           @RequestParam(defaultValue = "1") Integer page) {
        Page<EnrollmentWithSimpleLectureResponse> enrollments = enrollmentService.getEnrollmentWithSimpleLectureResponses(user, false, page);
        return ResponseEntity.ok(enrollments);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("수강내역 조회")
    @GetMapping("/{enrollment_id}")
    public ResponseEntity<?> getEnrollment(@CurrentUser User user, @PathVariable(name = "enrollment_id") Long enrollmentId) {
        EnrollmentWithSimpleLectureResponse enrollment = enrollmentService.getEnrollmentWithSimpleLectureResponse(user, enrollmentId);
        return ResponseEntity.ok(enrollment);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("리뷰 작성")
    @PostMapping("/{enrollment_id}/reviews")
    public ResponseEntity<?> newReview(@CurrentUser User user, @PathVariable(name = "enrollment_id") Long enrollmentId,
                                       @Validated @RequestBody MenteeReviewCreateRequest menteeReviewCreateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        menteeReviewService.createMenteeReview(user, enrollmentId, menteeReviewCreateRequest);
        return created();
    }

}
