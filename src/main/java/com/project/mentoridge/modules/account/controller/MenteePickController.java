package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.controller.response.PickWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.service.PickService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"MenteePickController"})
@RequestMapping("/api/mentees/my-picks")
@RestController
@RequiredArgsConstructor
public class MenteePickController {

    private final PickService pickService;

    @GetMapping
    public ResponseEntity<?> getPicks(@CurrentUser User user,
                                      @RequestParam(defaultValue = "1") Integer page) {
        Page<PickWithSimpleLectureResponse> picks = pickService.getPickWithSimpleLectureResponses(user, page);
        return ResponseEntity.ok(picks);
    }
//
//    @DeleteMapping("/{pick_id}")
//    public ResponseEntity<?> subtractPick(@CurrentUser User user,
//                                          @PathVariable(name = "pick_id") Long pickId) {
//        pickService.deletePick(user, pickId);
//        return ok();
//    }

    @DeleteMapping
    public ResponseEntity<?> clear(@CurrentUser User user) {

        pickService.deleteAllPicks(user);
        return ok();
    }
}
