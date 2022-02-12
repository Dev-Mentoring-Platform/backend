package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.service.MentorCancellationService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.controller.response.CancellationResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"MentorCancellationController"})
@RequestMapping("/api/mentors/my-cancellations")
@RestController
@RequiredArgsConstructor
public class MentorCancellationController {
// TODO - test
    private final MentorCancellationService mentorCancellationService;

    @ApiOperation("환불 목록 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getMyCancellations(@CurrentUser User user,
                                                @RequestParam(name = "page", defaultValue = "1") Integer page) {
        Page<CancellationResponse> cancellations = mentorCancellationService.getCancellationResponses(user, page);
        return ResponseEntity.ok(cancellations);
    }

    @ApiOperation("환불 승인")
    @PutMapping("/{cancellation_id}")
    public ResponseEntity<?> approveCancellation(@CurrentUser User user,
                                                 @PathVariable(name = "cancellation_id") Long cancellationId) {
        mentorCancellationService.approve(user, cancellationId);
        return ok();
    }




}
