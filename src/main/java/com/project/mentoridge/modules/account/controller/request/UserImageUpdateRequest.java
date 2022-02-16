package com.project.mentoridge.modules.account.controller.request;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserImageUpdateRequest {

    private String image;

    @Builder(access = AccessLevel.PUBLIC)
    private UserImageUpdateRequest(String image) {
        this.image = image;
    }
}
