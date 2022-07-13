package com.project.mentoridge.modules.account.controller.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserImageUpdateRequest {

    @NotBlank
    private String image;

    @Builder(access = AccessLevel.PUBLIC)
    private UserImageUpdateRequest(String image) {
        this.image = image;
    }
}
