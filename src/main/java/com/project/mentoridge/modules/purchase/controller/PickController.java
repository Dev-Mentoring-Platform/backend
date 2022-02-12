package com.project.mentoridge.modules.purchase.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.service.PickService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project.mentoridge.config.response.Response.created;

@Api(tags = {"PickController"})
@RestController
@RequiredArgsConstructor
public class PickController {

    private final PickService pickService;

    @ApiOperation("좋아요")
    //@ApiOperation("장바구니 추가")
    @PostMapping("/api/lectures/{lecture_id}/picks")
    public ResponseEntity<?> addPick(@CurrentUser User user,
                                     @PathVariable(name = "lecture_id") Long lectureId) {
        pickService.createPick(user, lectureId);
        return created();
    }

}
