package com.project.mentoridge.modules.review.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleEachLectureResponse;
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

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"MenteeReviewController"})
@RequestMapping("/api/mentees/my-reviews")
@RestController
@RequiredArgsConstructor
public class MenteeReviewController {

    private final MenteeReviewService menteeReviewService;

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("작성한 리뷰(+강의) 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getReviews(@CurrentUser User user,
                                        @RequestParam(defaultValue = "1") Integer page) {
        Page<ReviewWithSimpleEachLectureResponse> reviews = menteeReviewService.getReviewWithSimpleEachLectureResponses(user, page);
        return ResponseEntity.ok(reviews);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("리뷰 조회")
    @GetMapping("/{mentee_review_id}")
    public ResponseEntity<?> getReview(@PathVariable(name = "mentee_review_id") Long menteeReviewId) {
        ReviewWithSimpleEachLectureResponse review = menteeReviewService.getReviewWithSimpleEachLectureResponse(menteeReviewId);
        return ResponseEntity.ok(review);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("리뷰 수정")
    @PutMapping("/{mentee_review_id}")
    public ResponseEntity<?> editReview(@CurrentUser User user,
                                        @PathVariable(name = "mentee_review_id") Long menteeReviewId,
                                        @Validated @RequestBody MenteeReviewUpdateRequest menteeReviewUpdateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        menteeReviewService.updateMenteeReview(user, menteeReviewId, menteeReviewUpdateRequest);
        return ok();
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("리뷰 삭제")
    @DeleteMapping("/{mentee_review_id}")
    public ResponseEntity<?> deleteReview(@CurrentUser User user,
                                          @PathVariable(name = "mentee_review_id") Long menteeReviewId) {

        menteeReviewService.deleteMenteeReview(user, menteeReviewId);
        return ok();
    }

}
