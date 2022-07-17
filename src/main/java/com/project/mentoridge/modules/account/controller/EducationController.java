package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.controller.request.EducationCreateRequest;
import com.project.mentoridge.modules.account.controller.request.EducationUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.service.EducationService;
import com.project.mentoridge.modules.account.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"EducationController"})
@RequestMapping("/api/educations")
@RequiredArgsConstructor
@RestController
public class EducationController {

    private final EducationService educationService;

    // @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("Education 조회")
    @GetMapping(value = "/{education_id}")
    public ResponseEntity<?> getEducation(@CurrentUser User user,
                                          @PathVariable(name = "education_id") Long educationId) {
        // User user = checkMentorAuthority(principalDetails);
        // User user = principalDetails.getUser();
        EducationResponse education = educationService.getEducationResponse(user, educationId);
        return ResponseEntity.ok(education);
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("Education 등록")
    @PostMapping
    public ResponseEntity<?> newEducation(@CurrentUser User user,
                                          @Validated @RequestBody EducationCreateRequest educationCreateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        // User user = checkMentorAuthority(principalDetails);
        educationService.createEducation(user, educationCreateRequest);
        return created();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("Education 수정")
    @PutMapping("/{education_id}")
    public ResponseEntity<?> editEducation(@CurrentUser User user,
                                           @PathVariable(name = "education_id") Long educationId,
                                           @Validated @RequestBody EducationUpdateRequest educationUpdateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        // User user = checkMentorAuthority(principalDetails);
        educationService.updateEducation(user, educationId, educationUpdateRequest);
        return ok();
    }

    @PreAuthorize("hasRole('ROLE_MENTOR')")
    @ApiOperation("Education 삭제")
    @DeleteMapping("/{education_id}")
    public ResponseEntity<?> deleteEducation(@CurrentUser User user, @PathVariable(name = "education_id") Long educationId) {
        // User user = checkMentorAuthority(principalDetails);
        educationService.deleteEducation(user, educationId);
        return ok();
    }

}
