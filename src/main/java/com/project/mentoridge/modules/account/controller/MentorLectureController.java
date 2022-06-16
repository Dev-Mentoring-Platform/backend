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
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
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
import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"MentorLectureController"})
@RequestMapping("/api/mentors/my-lectures")
@RestController
@RequiredArgsConstructor
public class MentorLectureController {

    private final MentorLectureService mentorLectureService;
    private final LectureService lectureService;

    private final MenteeReviewService menteeReviewService;
    private final MentorReviewService mentorReviewService;

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("등록 강의 전체 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getLectures(@CurrentUser User user,
                                         @RequestParam(defaultValue = "1") Integer page) {

        Page<LectureResponse> lectures = mentorLectureService.getLectureResponses(user, page);
        return ResponseEntity.ok(lectures);
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("등록 강의 개별 조회")
    @GetMapping("/{lecture_id}")
    public ResponseEntity<?> getLecture(@CurrentUser User user, @PathVariable(name = "lecture_id") Long lectureId) {

        LectureResponse lecture = lectureService.getLectureResponse(user, lectureId);
        // lectureMapstructUtil.getLectureResponse(lecture);
        return ResponseEntity.ok(lecture);
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("등록 강의별 리뷰 조회 - 페이징")
    @GetMapping("/{lecture_id}/reviews")
    public ResponseEntity<?> getReviewsOfLecture(@PathVariable(name = "lecture_id") Long lectureId,
                                                 @RequestParam(defaultValue = "1") Integer page) {

        Page<ReviewResponse> reviews = menteeReviewService.getReviewResponsesOfLecture(lectureId, page);
        return ResponseEntity.ok(reviews);
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("등록 강의별 리뷰 개별 조회")
    @GetMapping("/{lecture_id}/reviews/{mentee_review_id}")
    public ResponseEntity<?> getReviewOfLecture(@PathVariable(name = "lecture_id") Long lectureId,
                                                @PathVariable(name = "mentee_review_id") Long menteeReviewId) {

        ReviewResponse review = menteeReviewService.getReviewResponseOfLecture(lectureId, menteeReviewId);
        return ResponseEntity.ok(review);
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("멘토 리뷰 작성")
    @PostMapping("/{lecture_id}/reviews/{mentee_review_id}")
    public ResponseEntity<?> newReview(@CurrentUser User user,
                                       @PathVariable(name = "lecture_id") Long lectureId,
                                       @PathVariable(name = "mentee_review_id") Long menteeReviewId,
                                       @Validated @RequestBody MentorReviewCreateRequest mentorReviewCreateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        mentorReviewService.createMentorReview(user, lectureId, menteeReviewId, mentorReviewCreateRequest);
        return created();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("멘토 리뷰 수정")
    @PutMapping("/{lecture_id}/reviews/{mentee_review_id}/children/{mentor_review_id}")
    public ResponseEntity<?> editReview(@CurrentUser User user,
                                        @PathVariable(name = "lecture_id") Long lectureId,
                                        @PathVariable(name = "mentee_review_id") Long menteeReviewId,
                                        @PathVariable(name = "mentor_review_id") Long mentorReviewId,
                                        @Validated @RequestBody MentorReviewUpdateRequest mentorReviewUpdateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        mentorReviewService.updateMentorReview(user, lectureId, menteeReviewId, mentorReviewId, mentorReviewUpdateRequest);
        return ok();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("멘토 리뷰 삭제")
    @DeleteMapping("/{lecture_id}/reviews/{mentee_review_id}/children/{mentor_review_id}")
    public ResponseEntity<?> deleteReview(@CurrentUser User user,
                                          @PathVariable(name = "lecture_id") Long lectureId,
                                          @PathVariable(name = "mentee_review_id") Long menteeReviewId,
                                          @PathVariable(name = "mentor_review_id") Long mentorReviewId) {

        mentorReviewService.deleteMentorReview(user, lectureId, menteeReviewId, mentorReviewId);
        return ok();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("등록 강의별 멘티 조회 - 페이징")
    @GetMapping("/{lecture_id}/mentees")
    public ResponseEntity<?> getMenteesOfLecture(@CurrentUser User user,
                                                @PathVariable(name = "lecture_id") Long lectureId,
                                                @RequestParam(defaultValue = "1") Integer page) {

        Page<MenteeResponse> mentees = mentorLectureService.getMenteeResponsesOfLecture(user, lectureId, page);
        return ResponseEntity.ok(mentees);
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("등록 강의별 수강내역 조회 - 페이징")
    @GetMapping("/{lecture_id}/enrollments")
    public ResponseEntity<?> getEnrollmentsOfLecture(@CurrentUser User user,
                                                     @PathVariable(name = "lecture_id") Long lectureId,
                                                     @RequestParam(defaultValue = "1") Integer page) {

        Page<EnrollmentResponse> enrollments = mentorLectureService.getEnrollmentResponsesOfLecture(user, lectureId, page);
        return ResponseEntity.ok(enrollments);
    }
/*
    @ApiOperation("강의 close")
    @PutMapping("/{lecture_id}/close")
    public ResponseEntity<?> close(@CurrentUser User user, @PathVariable(name = "lecture_id") Long lectureId) {
        lectureService.close(user, lectureId);
        return ok();
    }

    @ApiOperation("강의 open")
    @PutMapping("/{lecture_id}/open")
    public ResponseEntity<?> open(@CurrentUser User user, @PathVariable(name = "lecture_id") Long lectureId) {
        lectureService.open(user, lectureId);
        return ok();
    }*/

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("강의 모집 종료")
    @PutMapping("/{lecture_id}/lecturePrices/{lecture_price_id}/close")
    public ResponseEntity<?> close(@CurrentUser User user, @PathVariable(name = "lecture_id") Long lectureId,
                                   @PathVariable(name = "lecture_price_id") Long lecturePriceId) {
        lectureService.close(user, lectureId, lecturePriceId);
        return ok();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("강의 모집")
    @PutMapping("/{lecture_id}/lecturePrices/{lecture_price_id}/open")
    public ResponseEntity<?> open(@CurrentUser User user, @PathVariable(name = "lecture_id") Long lectureId,
                                  @PathVariable(name = "lecture_price_id") Long lecturePriceId) {
        lectureService.open(user, lectureId, lecturePriceId);
        return ok();
    }
}
