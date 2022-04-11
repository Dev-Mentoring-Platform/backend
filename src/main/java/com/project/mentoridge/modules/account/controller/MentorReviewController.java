package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleLectureResponse;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"MentorReviewController"})
@RequestMapping("/api/mentors/my-reviews")
@RestController
@RequiredArgsConstructor
public class MentorReviewController {

    private final MentorReviewService mentorReviewService;

    // TODO - 유저별 리뷰 확인 : /my-reviews

    // TODO - 내가(멘토가) 작성한 리뷰 조회
//    @ApiOperation("작성한 리뷰 조회 - 페이징")
//    @GetMapping
//    public ResponseEntity<?> getReviews(@CurrentUser User user,
//                                        @RequestParam(defaultValue = "1") Integer page) {
//        return null;
//    }
//
//    @ApiOperation("리뷰 조회")
//    @GetMapping("/{review_id}")
//    public ResponseEntity<?> getReview(@PathVariable(name = "review_id") Long reviewId) {
//        return null;
//    }

    @ApiOperation("내 멘티가 작성한 내 리뷰 리스트 - 페이징")
    @GetMapping("/by-mentees")
    public ResponseEntity<?> getMyReviewsByMyMentees(@CurrentUser User user,
                                                     @RequestParam(defaultValue = "1") Integer page) {
        Page<ReviewWithSimpleLectureResponse> lectures = mentorReviewService.getReviewWithSimpleLectureResponsesOfMentorByMentees(user, page);
        return ResponseEntity.ok(lectures);
    }

}
