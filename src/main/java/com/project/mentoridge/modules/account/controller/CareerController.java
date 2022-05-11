package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.controller.request.CareerCreateRequest;
import com.project.mentoridge.modules.account.controller.request.CareerUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.service.CareerService;
import com.project.mentoridge.modules.account.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;
import static com.project.mentoridge.modules.base.AbstractController.checkMentorAuthority;

@Api(tags = {"CareerController"})
@RequestMapping("/api/careers")
@RequiredArgsConstructor
@RestController
public class CareerController {

    private final CareerService careerService;

    // @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("Career 조회")
    @GetMapping("/{career_id}")
    public ResponseEntity<?> getCareer(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @PathVariable(name = "career_id") Long careerId) {
        // User user = checkMentorAuthority(principalDetails);
        User user = principalDetails.getUser();
        CareerResponse career = careerService.getCareerResponse(user, careerId);
        return ResponseEntity.ok(career);
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("Career 등록")
    @PostMapping
    public ResponseEntity<?> newCareer(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @Valid @RequestBody CareerCreateRequest careerCreateRequest) {
        User user = checkMentorAuthority(principalDetails);
        careerService.createCareer(user, careerCreateRequest);
        return created();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("Career 수정")
    @PutMapping("/{career_id}")
    public ResponseEntity<?> editCareer(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @PathVariable(name = "career_id") Long careerId,
                                        @Valid @RequestBody CareerUpdateRequest careerUpdateRequest) {
        User user = checkMentorAuthority(principalDetails);
        careerService.updateCareer(user, careerId, careerUpdateRequest);
        return ok();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("Career 삭제")
    @DeleteMapping("/{career_id}")
    public ResponseEntity<?> deleteCareer(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                          @PathVariable(name = "career_id") Long careerId) {
        User user = checkMentorAuthority(principalDetails);
        careerService.deleteCareer(user, careerId);
        return ok();
    }

}
