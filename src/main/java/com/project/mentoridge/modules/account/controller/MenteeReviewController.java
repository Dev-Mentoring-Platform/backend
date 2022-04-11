package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleLectureResponse;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"MenteeReviewController"})
@RequestMapping("/api/mentees/my-reviews")
@RestController
@RequiredArgsConstructor
public class MenteeReviewController {

    private final MenteeReviewService menteeReviewService;
    private final EnrollmentService enrollmentService;

    // TODO - 강의 추가
    @ApiOperation("작성한 리뷰(+강의) 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getReviews(@CurrentUser User user,
                                        @RequestParam(defaultValue = "1") Integer page) {
        Page<ReviewWithSimpleLectureResponse> reviews = menteeReviewService.getReviewWithSimpleLectureResponses(user, page);
        return ResponseEntity.ok(reviews);
    }

    @ApiOperation("리뷰 조회")
    @GetMapping("/{mentee_review_id}")
    public ResponseEntity<?> getReview(@PathVariable(name = "mentee_review_id") Long menteeReviewId) {
        ReviewWithSimpleLectureResponse review = menteeReviewService.getReviewWithSimpleLectureResponse(menteeReviewId);
        return ResponseEntity.ok(review);
    }

    @ApiOperation("리뷰 미작성 강의 리스트 - 페이징")
    @GetMapping("/unreviewed")
    public ResponseEntity<?> getUnreviewedLecturesOfMentee(@CurrentUser User user,
                                                           @RequestParam(defaultValue = "1") Integer page) {
        Page<EnrollmentWithSimpleLectureResponse> lectures = enrollmentService.getEnrollmentWithSimpleLectureResponses(user, false, page);
        return ResponseEntity.ok(lectures);
    }

}
