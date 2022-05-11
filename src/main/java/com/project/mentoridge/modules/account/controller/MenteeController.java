package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.controller.request.MenteeUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.service.MenteeService;
import com.project.mentoridge.modules.account.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"MenteeController"})
@RequestMapping("/api/mentees")
@RestController
@RequiredArgsConstructor
public class MenteeController {

    private final MenteeService menteeService;

    // TODO - 검색
    @ApiOperation("멘티 전체 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getMentees(@RequestParam(defaultValue = "1") Integer page) {

        Page<MenteeResponse> mentees = menteeService.getMenteeResponses(page);
        return ResponseEntity.ok(mentees);
    }

    @ApiOperation("멘티 조회")
    @GetMapping("/{mentee_id}")
    public ResponseEntity<?> getMentee(@PathVariable(name = "mentee_id") Long menteeId) {

        MenteeResponse mentee = menteeService.getMenteeResponse(menteeId);
        return ResponseEntity.ok(mentee);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("멘티 정보 수정")
    @PutMapping("/my-info")
    public ResponseEntity<?> editMentee(@CurrentUser User user, @Valid @RequestBody MenteeUpdateRequest menteeUpdateRequest) {

        // TODO - CHECK : Bearer Token 없이 요청하는 경우
        // user = null
        // .antMatchers(HttpMethod.PUT, "/**").authenticated()
        menteeService.updateMentee(user, menteeUpdateRequest);
        return ok();
    }

}
