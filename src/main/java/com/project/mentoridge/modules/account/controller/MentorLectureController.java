package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.service.MentorLectureService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentResponse;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
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

@Api(tags = {"MentorLectureController"})
@RequestMapping("/api/mentors/my-lectures")
@RestController
@RequiredArgsConstructor
public class MentorLectureController {

    private final MentorLectureService mentorLectureService;
    private final LectureService lectureService;
    private final ReviewService reviewService;

    @ApiOperation("등록 강의 전체 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getLectures(@CurrentUser User user,
                                         @RequestParam(defaultValue = "1") Integer page) {

        Page<LectureResponse> lectures = mentorLectureService.getLectureResponses(user, page);
        return ResponseEntity.ok(lectures);
    }

    // TODO - CHECK : user
    @ApiOperation("등록 강의 개별 조회")
    @GetMapping("/{lecture_id}")
    public ResponseEntity<?> getLecture(@CurrentUser User user, @PathVariable(name = "lecture_id") Long lectureId) {

        LectureResponse lecture = lectureService.getLectureResponse(user, lectureId);
        // lectureMapstructUtil.getLectureResponse(lecture);
        return ResponseEntity.ok(lecture);
    }

    @ApiOperation("등록 강의별 리뷰 조회 - 페이징")
    @GetMapping("/{lecture_id}/reviews")
    public ResponseEntity<?> getReviewsOfLecture(@PathVariable(name = "lecture_id") Long lectureId,
                                                 @RequestParam(defaultValue = "1") Integer page) {

        Page<ReviewResponse> reviews = reviewService.getReviewResponsesOfLecture(lectureId, page);
        return ResponseEntity.ok(reviews);
    }

    @ApiOperation("등록 강의별 리뷰 개별 조회")
    @GetMapping("/{lecture_id}/reviews/{review_id}")
    public ResponseEntity<?> getReviewOfLecture(@PathVariable(name = "lecture_id") Long lectureId,
                                                @PathVariable(name = "review_id") Long reviewId) {

        ReviewResponse review = reviewService.getReviewResponseOfLecture(lectureId, reviewId);
        return ResponseEntity.ok(review);
    }

    @ApiOperation("멘토 리뷰 작성")
    @PostMapping("/{lecture_id}/reviews/{parent_id}")
    public ResponseEntity<?> newReview(@CurrentUser User user,
                                       @PathVariable(name = "lecture_id") Long lectureId,
                                       @PathVariable(name = "parent_id") Long parentId,
                                       @RequestBody @Valid MentorReviewCreateRequest mentorReviewCreateRequest) {

        reviewService.createMentorReview(user, lectureId, parentId, mentorReviewCreateRequest);
        return created();
    }

    @ApiOperation("멘토 리뷰 수정")
    @PutMapping("/{lecture_id}/reviews/{parent_id}/children/{review_id}")
    public ResponseEntity<?> editReview(@CurrentUser User user,
                                        @PathVariable(name = "lecture_id") Long lectureId,
                                        @PathVariable(name = "parent_id") Long parentId,
                                        @PathVariable(name = "review_id") Long reviewId,
                                        @RequestBody @Valid MentorReviewUpdateRequest mentorReviewUpdateRequest) {

        reviewService.updateMentorReview(user, lectureId, parentId, reviewId, mentorReviewUpdateRequest);
        return ok();
    }

    @ApiOperation("멘토 리뷰 삭제")
    @DeleteMapping("/{lecture_id}/reviews/{parent_id}/children/{review_id}")
    public ResponseEntity<?> deleteReview(@CurrentUser User user,
                                          @PathVariable(name = "lecture_id") Long lectureId,
                                          @PathVariable(name = "parent_id") Long parentId,
                                          @PathVariable(name = "review_id") Long reviewId) {

        reviewService.deleteMentorReview(user, lectureId, parentId, reviewId);
        return ok();
    }

    @ApiOperation("등록 강의별 멘티 조회 - 페이징")
    @GetMapping("/{lecture_id}/mentees")
    public ResponseEntity<?> getMenteesOfLecture(@CurrentUser User user,
                                                @PathVariable(name = "lecture_id") Long lectureId,
                                                @RequestParam(defaultValue = "1") Integer page) {

        Page<MenteeResponse> mentees = mentorLectureService.getMenteeResponsesOfLecture(user, lectureId, page);
        return ResponseEntity.ok(mentees);
    }

    @ApiOperation("등록 강의별 수강내역 조회 - 페이징")
    @GetMapping("/{lecture_id}/enrollments")
    public ResponseEntity<?> getEnrollmentsOfLecture(@CurrentUser User user,
                                                     @PathVariable(name = "lecture_id") Long lectureId,
                                                     @RequestParam(defaultValue = "1") Integer page) {

        Page<EnrollmentResponse> enrollments = mentorLectureService.getEnrollmentResponsesOfLecture(user, lectureId, page);
        return ResponseEntity.ok(enrollments);
    }

    // 멘티가 강의 종료
//    @ApiOperation("강의 종료")
//    @PutMapping("/{lecture_id}/enrollments/{enrollment_id}")
//    public ResponseEntity<?> close(@CurrentUser User user,
//                                   @PathVariable(name = "lecture_id") Long lectureId,
//                                   @PathVariable(name = "enrollment_id") Long enrollmentId) {
//        enrollmentService.close(user, lectureId, enrollmentId);
//        return ResponseEntity.ok().build();
//    }
}
