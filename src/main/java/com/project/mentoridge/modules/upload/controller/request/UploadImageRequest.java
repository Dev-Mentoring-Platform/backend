package com.project.mentoridge.modules.upload.controller.request;

import com.project.mentoridge.config.validation.Extension;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UploadImageRequest {

    @Extension(message = "png, jpg, jpeg, gif, pdf 확장자 파일만 선택해주세요.", allows = {"png", "jpg", "jpeg", "gif", "pdf"})
    @NotNull(message = "파일을 선택해주세요.")
    private MultipartFile file;

    @Builder(access = AccessLevel.PUBLIC)
    private UploadImageRequest(@NotNull(message = "파일을 선택해주세요.") MultipartFile file) {
        this.file = file;
    }

}