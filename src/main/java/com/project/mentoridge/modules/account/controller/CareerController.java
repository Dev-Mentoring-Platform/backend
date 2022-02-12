package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.controller.request.CareerCreateRequest;
import com.project.mentoridge.modules.account.controller.request.CareerUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.service.CareerService;
import com.project.mentoridge.modules.account.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"CareerController"})
@RequestMapping("/api/careers")
@RequiredArgsConstructor
@RestController
public class CareerController {

    private final CareerService careerService;

    @ApiOperation("Career 조회")
    @GetMapping("/{career_id}")
    public ResponseEntity<?> getCareer(@CurrentUser User user,
                                       @PathVariable(name = "career_id") Long careerId) {
        CareerResponse career = careerService.getCareerResponse(user, careerId);
        return ResponseEntity.ok(career);
    }

    @ApiOperation("Career 등록")
    @PostMapping
    public ResponseEntity<?> newCareer(@CurrentUser User user,
                                       @Valid @RequestBody CareerCreateRequest careerCreateRequest) {
        careerService.createCareer(user, careerCreateRequest);
        return created();
    }

    @ApiOperation("Career 수정")
    @PutMapping("/{career_id}")
    public ResponseEntity<?> editCareer(@CurrentUser User user,
                                        @PathVariable(name = "career_id") Long careerId,
                                        @Valid @RequestBody CareerUpdateRequest careerUpdateRequest) {
        careerService.updateCareer(user, careerId, careerUpdateRequest);
        return ok();
    }

    @ApiOperation("Career 삭제")
    @DeleteMapping("/{career_id}")
    public ResponseEntity<?> deleteCareer(@CurrentUser User user,
                                          @PathVariable(name = "career_id") Long careerId) {
        careerService.deleteCareer(user, careerId);
        return ok();
    }

}
