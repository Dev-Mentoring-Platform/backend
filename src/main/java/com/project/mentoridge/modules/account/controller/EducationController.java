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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"EducationController"})
@RequestMapping("/api/educations")
@RequiredArgsConstructor
@RestController
public class EducationController {

    private final EducationService educationService;

    @ApiOperation("Education 조회")
    @GetMapping("/{education_id}")
    public ResponseEntity<?> getEducation(@CurrentUser User user,
                                          @PathVariable(name = "education_id") Long educationId) {
        EducationResponse education = educationService.getEducationResponse(user, educationId);
        return ResponseEntity.ok(education);
    }

    @ApiOperation("Education 등록")
    @PostMapping
    public ResponseEntity<?> newEducation(@CurrentUser User user,
                                          @Valid @RequestBody EducationCreateRequest educationCreateRequest) {
        educationService.createEducation(user, educationCreateRequest);
        return created();
    }

    @ApiOperation("Education 수정")
    @PutMapping("/{education_id}")
    public ResponseEntity<?> editEducation(@CurrentUser User user,
                                           @PathVariable(name = "education_id") Long educationId,
                                           @Valid @RequestBody EducationUpdateRequest educationUpdateRequest) {
        educationService.updateEducation(user, educationId, educationUpdateRequest);
        return ok();
    }

    @ApiOperation("Education 삭제")
    @DeleteMapping("/{education_id}")
    public ResponseEntity<?> deleteEducation(@CurrentUser User user,
                                             @PathVariable(name = "education_id") Long educationId) {
        educationService.deleteEducation(user, educationId);
        return ok();
    }

}
