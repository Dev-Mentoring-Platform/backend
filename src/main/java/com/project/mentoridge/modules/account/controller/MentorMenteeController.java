package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.controller.response.MenteeEnrollmentInfoResponse;
import com.project.mentoridge.modules.account.controller.response.MenteeSimpleResponse;
import com.project.mentoridge.modules.account.service.MentorMenteeService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"MentorMenteeController"})
@RequestMapping("/api/mentors/my-mentees")
@RestController
@RequiredArgsConstructor
public class MentorMenteeController {

    private final MentorMenteeService mentorMenteeService;
    private final MenteeReviewService menteeReviewService;

    // 진행중인 강의 멘티 리스트
    // 종료된 강의 멘티 리스트
    @ApiOperation("멘티 전체 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getMyMentees(@CurrentUser User user,
                                         @RequestParam(name = "closed", required = false, defaultValue = "false") Boolean closed,
                                         @RequestParam(name = "page", defaultValue = "1") Integer page) {
        Page<MenteeSimpleResponse> mentees = mentorMenteeService.getMenteeSimpleResponses(user, closed, page);
        return ResponseEntity.ok(mentees);
    }

    @ApiOperation("멘티-강의 조회 - 페이징")
    @GetMapping("/{mentee_id}")
    public ResponseEntity<?> getMyMenteesAndEnrollmentInfo(@CurrentUser User user,
                                        @PathVariable(name = "mentee_id") Long menteeId,
                                        @RequestParam(name = "page", defaultValue = "1") Integer page) {
        Page<MenteeEnrollmentInfoResponse> menteeEnrollmentInfos = mentorMenteeService.getMenteeLectureResponses(user, menteeId, page);
        return ResponseEntity.ok(menteeEnrollmentInfos);
    }

    @ApiOperation("멘티 리뷰 조회")
    @GetMapping("/{mentee_id}/enrollments/{enrollment_id}/reviews/{mentee_review_id}")
    public ResponseEntity<?> getReviewsOfMyMentee(@CurrentUser User user,
                                                 @PathVariable(name = "mentee_id") Long menteeId,
                                                 @PathVariable(name = "enrollment_id") Long enrollmentId,
                                                 @PathVariable(name = "mentee_review_id") Long menteeReviewId) {
        ReviewResponse review = menteeReviewService.getReviewResponseOfEnrollment(menteeId, enrollmentId, menteeReviewId);
        return ResponseEntity.ok(review);
    }

}
