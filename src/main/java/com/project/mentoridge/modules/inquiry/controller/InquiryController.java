package com.project.mentoridge.modules.inquiry.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.inquiry.controller.request.InquiryCreateRequest;
import com.project.mentoridge.modules.inquiry.service.InquiryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.created;

@Api(tags = {"InquiryController"})
@RequestMapping("/api/users/my-inquiry")
@RequiredArgsConstructor
@RestController
public class InquiryController {

    private final InquiryService inquiryService;

    @ApiOperation("Inquiry 등록")
    @PostMapping
    public ResponseEntity<?> newInquiry(@CurrentUser User user,
                                        @Validated @RequestBody InquiryCreateRequest inquiryCreateRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        inquiryService.createInquiry(user, inquiryCreateRequest);
        return created();
    }

//    // TODO - TEST : 예외 처리
//    @ApiIgnore
//    @PostMapping("/test-producer")
//    public ResponseEntity<?> test(@RequestBody InquiryCreateRequest inquiryCreateRequest) throws JsonProcessingException {
//        inquiryService.test(inquiryCreateRequest);
//        return ResponseEntity.ok().build();
//    }

}
