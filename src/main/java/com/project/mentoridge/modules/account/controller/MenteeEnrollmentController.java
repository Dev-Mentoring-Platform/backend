package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.LecturePriceWithLectureResponse;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
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

    private final EnrollmentService enrollmentService;
    private final LectureService lectureService;
    private final MenteeReviewService menteeReviewService;

    @ApiOperation("수강 강의 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getLectures(@CurrentUser User user,
                                         @RequestParam(defaultValue = "1") Integer page) {

        Page<LecturePriceWithLectureResponse> lectures = enrollmentService.getLecturePriceWithLectureResponsesOfMentee(user, page);
        return ResponseEntity.ok(lectures);
    }

    @ApiOperation("수강 강의 개별 조회")
    @GetMapping("/{lecture_id}/lecturePrices/{lecture_price_id}")
    public ResponseEntity<?> getLecture(@CurrentUser User user, @PathVariable(name = "lecture_id") Long lectureId,
                                        @PathVariable(name = "lecture_price_id") Long lecturePriceId) {
        LecturePriceWithLectureResponse lecture = lectureService.getLectureResponsePerLecturePrice(user, lectureId, lecturePriceId);
        return ResponseEntity.ok(lecture);
    }

    // TODO - CHECK : user가 필요한가?
    @ApiOperation("수강 강의별 리뷰 조회 - 페이징")
    @GetMapping("/{lecture_id}/reviews")
    public ResponseEntity<?> getReviewsOfLecture(@PathVariable(name = "lecture_id") Long lectureId,
                                                 @RequestParam(defaultValue = "1") Integer page) {

        Page<ReviewResponse> reviews = menteeReviewService.getReviewResponsesOfLecture(lectureId, page);
        return ResponseEntity.ok(reviews);
    }

    // TODO - CHECK : user가 필요한가?
    @ApiOperation("수강 강의별 리뷰 개별 조회")
    @GetMapping("/{lecture_id}/reviews/{mentee_review_id}")
    public ResponseEntity<?> getReviewOfLecture(@PathVariable(name = "lecture_id") Long lectureId,
                                                @PathVariable(name = "mentee_review_id") Long menteeReviewId) {
        ReviewResponse review = menteeReviewService.getReviewResponseOfLecture(lectureId, menteeReviewId);
        return ResponseEntity.ok(review);
    }

    @ApiOperation("멘티 리뷰 작성")
    @PostMapping("/{lecture_id}/reviews")
    public ResponseEntity<?> newReview(@CurrentUser User user,
                                       @PathVariable(name = "lecture_id") Long lectureId,
                                       @RequestBody @Valid MenteeReviewCreateRequest menteeReviewCreateRequest) {

        menteeReviewService.createMenteeReview(user, lectureId, menteeReviewCreateRequest);
        return created();
    }

    @ApiOperation("멘티 리뷰 수정")
    @PutMapping("/{lecture_id}/reviews/{mentee_review_id}")
    public ResponseEntity<?> editReview(@CurrentUser User user,
                                        @PathVariable(name = "lecture_id") Long lectureId,
                                        @PathVariable(name = "mentee_review_id") Long menteeReviewId,
                                        @RequestBody @Valid MenteeReviewUpdateRequest menteeReviewUpdateRequest) {

        menteeReviewService.updateMenteeReview(user, lectureId, menteeReviewId, menteeReviewUpdateRequest);
        return ok();
    }

    @ApiOperation("멘티 리뷰 삭제")
    @DeleteMapping("/{lecture_id}/reviews/{mentee_review_id}")
    public ResponseEntity<?> deleteReview(@CurrentUser User user,
                                          @PathVariable(name = "lecture_id") Long lectureId,
                                          @PathVariable(name = "mentee_review_id") Long menteeReviewId) {

        menteeReviewService.deleteMenteeReview(user, lectureId, menteeReviewId);
        return ok();
    }

}
