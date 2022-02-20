package com.project.mentoridge.modules.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

//@Api(tags = {"MentorCancellationController"})
//@RequestMapping("/api/mentors/my-cancellations")
@RestController
@RequiredArgsConstructor
public class _MentorCancellationController {
/*
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
    }*/
}
