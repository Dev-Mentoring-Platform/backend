package com.project.mentoridge.modules.purchase.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.service.PickService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"PickController"})
@RestController
@RequiredArgsConstructor
public class PickController {

    private final PickService pickService;

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("좋아요 / 좋아요 취소")
    @PostMapping("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/picks")
    public ResponseEntity<?> addPick(@CurrentUser User user,
                                     @PathVariable(name = "lecture_id") Long lectureId,
                                     @PathVariable(name = "lecture_price_id") Long lecturePriceId) {
        return ResponseEntity.ok(pickService.createPick(user, lectureId, lecturePriceId));
    }

}
