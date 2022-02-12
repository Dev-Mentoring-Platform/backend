package com.project.mentoridge.modules.upload.controller;

import com.project.mentoridge.modules.upload.controller.request.UploadImageRequest;
import com.project.mentoridge.modules.upload.controller.response.UploadResponse;
import com.project.mentoridge.modules.upload.service.UploadService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = {"UploadController"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    // TODO - application.yml
    public static final String DIR = "user";

    private final UploadService uploadService;

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadImage(@ModelAttribute @Valid UploadImageRequest uploadImageRequest) {
        UploadResponse upload = uploadService.uploadImage(DIR, uploadImageRequest.getFile());
        return ResponseEntity.ok(upload);
    }

}
