package com.project.mentoridge.modules.lecture.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.config.security.Nullable;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureListRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"LectureController"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;
    private final MenteeReviewService menteeReviewService;

    // Field error in object 'user' on field 'zone' - User의 zone과 중복
    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("강의 목록 조회 - 위치별(멘토 주소 기준), 강의명, 개발언어, 온/오프라인, 개인/그룹, 레벨 필터")
    @GetMapping
    public ResponseEntity<?> getEachLectures(@CurrentUser User user,
                                             @RequestParam(name = "_zone", required = false) String zone,
                                             @Validated @ModelAttribute LectureListRequest lectureListRequest, BindingResult bindingResult,
                                             @RequestParam(defaultValue = "1") Integer page) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        Page<EachLectureResponse> lectures = lectureService.getEachLectureResponses(user, zone, lectureListRequest, page);
        return ResponseEntity.ok(lectures);
    }
/*
    @ApiOperation("강의 개별 조회")
    @GetMapping(value = "/{lecture_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLecture(@CurrentUser @Nullable User user, @PathVariable(name = "lecture_id") Long lectureId) {
        LectureResponse lecture = lectureService.getLectureResponse(user, lectureId);
        return ResponseEntity.ok(lecture);
    }*/

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("강의 개별 조회")
    @GetMapping(value = "/{lecture_id}/lecturePrices/{lecture_price_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEachLecture(@CurrentUser User user,
                                            @PathVariable(name = "lecture_id") Long lectureId,
                                            @PathVariable(name = "lecture_price_id") Long lecturePriceId) {
        EachLectureResponse lecture = lectureService.getEachLectureResponse(user, lectureId, lecturePriceId);
        return ResponseEntity.ok(lecture);
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("강의 등록")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> newLecture(@CurrentUser User user,
                                        @Validated(LectureCreateRequest.Order.class) @RequestBody LectureCreateRequest lectureCreateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        lectureService.createLecture(user, lectureCreateRequest);
        return created();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("강의 수정")
    @PutMapping("/{lecture_id}")
    public ResponseEntity<?> editLecture(@CurrentUser User user, @PathVariable(name = "lecture_id") Long lectureId,
                                         @Validated @RequestBody LectureUpdateRequest lectureUpdateRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        lectureService.updateLecture(user, lectureId, lectureUpdateRequest);
        return ok();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("강의 삭제")
    @DeleteMapping("/{lecture_id}")
    public ResponseEntity<?> deleteLecture(@CurrentUser User user,
                                           @PathVariable(name = "lecture_id") Long lectureId) {
        lectureService.deleteLecture(user, lectureId);
        return ok();
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("강의별 리뷰 리스트 - 페이징")
    @GetMapping("/{lecture_id}/lecturePrices/{lecture_price_id}/reviews")
    public ResponseEntity<?> getReviewsOfEachLecture(@PathVariable(name = "lecture_id") Long lectureId,
                                                 @PathVariable(name = "lecture_price_id") Long lecturePriceId,
                                                 @RequestParam(defaultValue = "1") Integer page) {
        Page<ReviewResponse> reviews = menteeReviewService.getReviewResponsesOfEachLecture(lectureId, lecturePriceId, page);
        return ResponseEntity.ok(reviews);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("강의 리뷰 개별 조회")
    @GetMapping("/{lecture_id}/lecturePrices/{lecture_price_id}/reviews/{mentee_review_id}")
    public ResponseEntity<?> getReviewOfEachLecture(@PathVariable(name = "lecture_id") Long lectureId,
                                                @PathVariable(name = "lecture_price_id") Long lecturePriceId,
                                                @PathVariable(name = "mentee_review_id") Long menteeReviewId) {
        ReviewResponse review = menteeReviewService.getReviewResponseOfEachLecture(lectureId, lecturePriceId, menteeReviewId);
        return ResponseEntity.ok(review);
    }

}
