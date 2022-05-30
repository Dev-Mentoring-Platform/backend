package com.project.mentoridge.modules.purchase.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"EnrollmentController"})
@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("강의 신청")
    @PostMapping("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/enrollments")
    public ResponseEntity<?> enroll(@CurrentUser User user,
                                    @PathVariable(name = "lecture_id") Long lectureId,
                                    @PathVariable(name = "lecture_price_id") Long lecturePriceId) {
        enrollmentService.createEnrollment(user, lectureId, lecturePriceId);
        return created();
    }

    // TODO - CHECK : 확인 테이블을 따로 생성하는 게 더 나은가?
    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("강의 신청 확인")
    @PutMapping("/api/enrollments/{enrollment_id}/check")
    public ResponseEntity<?> check(@CurrentUser User user, @PathVariable(name = "enrollment_id") Long enrollmentId) {
        enrollmentService.check(user, enrollmentId);
        return ok();
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("강의 종료")
    @PutMapping("/api/enrollments/{enrollment_id}/finish")
    public ResponseEntity<?> finish(@CurrentUser User user, @PathVariable(name = "enrollment_id") Long enrollmentId) {
        enrollmentService.finish(user, enrollmentId);
        return ok();
    }
}
