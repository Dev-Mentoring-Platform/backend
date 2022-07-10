package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.controller.request.MentorSignUpRequest;
import com.project.mentoridge.modules.account.controller.request.MentorUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.controller.response.MentorResponse;
import com.project.mentoridge.modules.account.service.MentorLectureService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.review.controller.response.ReviewListResponse;
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
import java.util.List;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"MentorController"})
@RequestMapping("/api/mentors")
@RestController
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;
    private final MentorLectureService mentorLectureService;
    private final MentorReviewService mentorReviewService;

    // TODO - 검색
    @ApiOperation("멘토 전체 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getMentors(@RequestParam(defaultValue = "1") Integer page) {

        Page<MentorResponse> mentors = mentorService.getMentorResponses(page);
        return ResponseEntity.ok(mentors);
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("내 멘토 정보 조회")
    @GetMapping("/my-info")
    public ResponseEntity<?> getMyInfo(@CurrentUser User user) {
        return ResponseEntity.ok(mentorService.getMentorResponse(user));
    }

    @ApiOperation("멘토 조회")
    @GetMapping("/{mentor_id}")
    public ResponseEntity<?> getMentor(@PathVariable(name = "mentor_id") Long mentorId) {

        MentorResponse mentor = mentorService.getMentorResponse(mentorId);
        return ResponseEntity.ok(mentor);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("멘토 등록")
    @PostMapping
    public ResponseEntity<?> newMentor(@CurrentUser User user,
                                       @Validated @RequestBody MentorSignUpRequest mentorSignUpRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        mentorService.createMentor(user, mentorSignUpRequest);
        return created();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("멘토 정보 수정")
    @PutMapping("/my-info")
    public ResponseEntity<?> editMentor(@CurrentUser User user,
                                        @Validated @RequestBody MentorUpdateRequest mentorUpdateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        mentorService.updateMentor(user, mentorUpdateRequest);
        return ok();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("멘토 탈퇴")
    @DeleteMapping
    public ResponseEntity<?> quitMentor(@CurrentUser User user) {

        mentorService.deleteMentor(user);
        return ok();
    }

    @ApiOperation("멘토의 Career 리스트")
    @GetMapping("/{mentor_id}/careers")
    public ResponseEntity<?> getCareers(@PathVariable(name = "mentor_id") Long mentorId) {

        List<CareerResponse> careers = mentorService.getCareerResponses(mentorId);
        return ResponseEntity.ok(careers);
    }

    @ApiOperation("멘토의 Education 리스트")
    @GetMapping("/{mentor_id}/educations")
    public ResponseEntity<?> getEducations(@PathVariable(name = "mentor_id") Long mentorId) {

        List<EducationResponse> educations = mentorService.getEducationResponses(mentorId);
        return ResponseEntity.ok(educations);
    }

    @ApiOperation("멘토의 강의 리스트")
    @GetMapping("/{mentor_id}/lectures")
    public ResponseEntity<?> getEachLectures(@PathVariable(name = "mentor_id") Long mentorId, @RequestParam(defaultValue = "1") Integer page) {

        // 2022.04.03 - 강의 가격별로 출력
        Page<EachLectureResponse> lectures = mentorLectureService.getEachLectureResponses(mentorId, page);
        return ResponseEntity.ok(lectures);
    }

    @ApiOperation("멘토의 강의 개별 조회")
    @GetMapping("/{mentor_id}/lectures/{lecture_id}/lecturePrices/{lecture_price_id}")
    public ResponseEntity<?> getEachLecture(@PathVariable(name = "mentor_id") Long mentorId,
                                            @PathVariable(name = "lecture_id") Long lectureId,
                                            @PathVariable(name = "lecture_price_id") Long lecturePriceId) {
        EachLectureResponse lecture = mentorLectureService.getEachLectureResponse(mentorId, lectureId, lecturePriceId);
        return ResponseEntity.ok(lecture);
    }

    @ApiOperation("후기 조회")
    @GetMapping("/{mentor_id}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable(name = "mentor_id") Long mentorId,
                                        @RequestParam(defaultValue = "1") Integer page) {
        // 평점 + 후기 리스트
        ReviewListResponse reviews = mentorReviewService.getReviewWithSimpleEachLectureResponsesOfMentorByMentees(mentorId, page);
        return ResponseEntity.ok(reviews);
    }

}
