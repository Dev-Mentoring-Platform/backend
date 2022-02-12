package com.project.mentoridge.modules.purchase.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project.mentoridge.config.response.Response.created;

@Api(tags = {"EnrollmentController"})
@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @ApiOperation("강의 수강")
    @PostMapping("/api/lectures/{lecture_id}/{lecture_price_id}/enrollments")
    public ResponseEntity<?> enroll(@CurrentUser User user,
                                    @PathVariable(name = "lecture_id") Long lectureId,
                                    @PathVariable(name = "lecture_price_id") Long lecturePriceId) {
        enrollmentService.createEnrollment(user, lectureId, lecturePriceId);
        return created();
    }

}
