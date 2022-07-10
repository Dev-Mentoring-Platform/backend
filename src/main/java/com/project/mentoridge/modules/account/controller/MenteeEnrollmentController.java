package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithEachLectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleEachLectureResponse;
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

import static com.project.mentoridge.config.response.Response.created;

@Api(tags = {"MenteeEnrollmentController"})
@RequestMapping("/api/mentees/my-enrollments")
@RestController
@RequiredArgsConstructor
public class MenteeEnrollmentController {

    private final MenteeReviewService menteeReviewService;
    private final EnrollmentService enrollmentService;


    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("신청 강의 리스트 / 승인 예정 - 페이징")
    @GetMapping("/unchecked")
    public ResponseEntity<?> getUncheckedEnrollments(@CurrentUser User user, @RequestParam(defaultValue = "1") Integer page) {
        Page<EnrollmentWithEachLectureResponse> lectures = enrollmentService.getEnrollmentWithEachLectureResponsesOfMentee(user, false, page);
        return ResponseEntity.ok(lectures);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("신청 강의 리스트 / 승인 완료 - 페이징")
    @GetMapping("/checked")
    public ResponseEntity<?> getCheckedEnrollments(@CurrentUser User user, @RequestParam(defaultValue = "1") Integer page) {
        Page<EnrollmentWithEachLectureResponse> lectures = enrollmentService.getEnrollmentWithEachLectureResponsesOfMentee(user, true, page);
        return ResponseEntity.ok(lectures);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("수강 강의 개별 조회")
    @GetMapping("/{enrollment_id}/lecture")
    public ResponseEntity<?> getEnrolledLecture(@CurrentUser User user, @PathVariable(name = "enrollment_id") Long enrollmentId) {
        EachLectureResponse lecture = enrollmentService.getEachLectureResponseOfEnrollment(user, enrollmentId, true);
        return ResponseEntity.ok(lecture);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("리뷰 미작성 수강내역 리스트 - 페이징")
    @GetMapping("/unreviewed")
    public ResponseEntity<?> getUnreviewedEnrollmentsOfMentee(@CurrentUser User user, @RequestParam(defaultValue = "1") Integer page) {
        Page<EnrollmentWithSimpleEachLectureResponse> enrollments = enrollmentService.getEnrollmentWithSimpleEachLectureResponses(user, false, page);
        return ResponseEntity.ok(enrollments);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("수강내역 조회")
    @GetMapping("/{enrollment_id}")
    public ResponseEntity<?> getEnrollment(@CurrentUser User user, @PathVariable(name = "enrollment_id") Long enrollmentId) {
        EnrollmentWithSimpleEachLectureResponse enrollment = enrollmentService.getEnrollmentWithSimpleEachLectureResponse(user, enrollmentId);
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
