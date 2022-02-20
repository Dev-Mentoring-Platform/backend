package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"MenteeLectureController"})
@RequestMapping("/api/mentees/my-lectures")
@RestController
@RequiredArgsConstructor
public class MenteeLectureController {

    private final EnrollmentService enrollmentService;
    private final LectureService lectureService;
    private final ReviewService reviewService;

    @ApiOperation("수강 강의 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getLectures(@CurrentUser User user,
                                         @RequestParam(defaultValue = "1") Integer page) {

        Page<LectureResponse> lectures = enrollmentService.getLectureResponsesOfMentee(user, page);
        return ResponseEntity.ok(lectures);
    }

    // TODO - CHECK : user
    @ApiOperation("수강 강의 개별 조회")
    @GetMapping("/{lecture_id}")
    public ResponseEntity<?> getLecture(@CurrentUser User user, @PathVariable(name = "lecture_id") Long lectureId) {
        LectureResponse lecture = lectureService.getLectureResponse(user, lectureId);
        return ResponseEntity.ok(lecture);
    }

/*    @ApiOperation("강의 수강 취소 요청")
    @PostMapping("/{lecture_id}/cancellations")
    public ResponseEntity<?> cancel(@CurrentUser User user,
                                    @PathVariable(name = "lecture_id") Long lectureId,
                                    @RequestBody @Valid CancellationCreateRequest cancellationCreateRequest) {
        cancellationService.cancel(user, lectureId, cancellationCreateRequest);
        return ok();
    }*/

    // TODO - CHECK : user가 필요한가?
    @ApiOperation("수강 강의별 리뷰 조회 - 페이징")
    @GetMapping("/{lecture_id}/reviews")
    public ResponseEntity<?> getReviewsOfLecture(@PathVariable(name = "lecture_id") Long lectureId,
                                                 @RequestParam(defaultValue = "1") Integer page) {

        Page<ReviewResponse> reviews = reviewService.getReviewResponsesOfLecture(lectureId, page);
        return ResponseEntity.ok(reviews);
    }

    // TODO - CHECK : user가 필요한가?
    @ApiOperation("수강 강의별 리뷰 개별 조회")
    @GetMapping("/{lecture_id}/reviews/{review_id}")
    public ResponseEntity<?> getReviewOfLecture(@PathVariable(name = "lecture_id") Long lectureId,
                                                @PathVariable(name = "review_id") Long reviewId) {

        ReviewResponse review = reviewService.getReviewResponseOfLecture(lectureId, reviewId);
        return ResponseEntity.ok(review);
    }

    @ApiOperation("멘티 리뷰 작성")
    @PostMapping("/{lecture_id}/reviews")
    public ResponseEntity<?> newReview(@CurrentUser User user,
                                       @PathVariable(name = "lecture_id") Long lectureId,
                                       @RequestBody @Valid MenteeReviewCreateRequest menteeReviewCreateRequest) {

        reviewService.createMenteeReview(user, lectureId, menteeReviewCreateRequest);
        return created();
    }

    @ApiOperation("멘티 리뷰 수정")
    @PutMapping("/{lecture_id}/reviews/{review_id}")
    public ResponseEntity<?> editReview(@CurrentUser User user,
                                        @PathVariable(name = "lecture_id") Long lectureId,
                                        @PathVariable(name = "review_id") Long reviewId,
                                        @RequestBody @Valid MenteeReviewUpdateRequest menteeReviewUpdateRequest) {

        reviewService.updateMenteeReview(user, lectureId, reviewId, menteeReviewUpdateRequest);
        return ok();
    }

    @ApiOperation("멘티 리뷰 삭제")
    @DeleteMapping("/{lecture_id}/reviews/{review_id}")
    public ResponseEntity<?> deleteReview(@CurrentUser User user,
                                          @PathVariable(name = "lecture_id") Long lectureId,
                                          @PathVariable(name = "review_id") Long reviewId) {

        reviewService.deleteMenteeReview(user, lectureId, reviewId);
        return ok();
    }

//    @ApiOperation("리뷰 작성하지 않은 강의 리스트 - 페이징")
//    @GetMapping("/unreviewed")
//    public ResponseEntity<?> getUnreviewedLectures(@CurrentUser User user) {
//        return null;
//    }

/*    @ApiOperation("강의 종료")
    @PutMapping("/{lecture_id}")
    public ResponseEntity<?> close(@CurrentUser User user,
                                   @PathVariable(name = "lecture_id") Long lectureId) {
        enrollmentService.close(user, lectureId);
        return ok();
    }*/
}
