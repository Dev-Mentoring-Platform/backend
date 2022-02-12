package com.project.mentoridge.modules.account.controller.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserImageUpdateRequest {

    private String image;

    public static UserImageUpdateRequest of(String image) {
        return new UserImageUpdateRequest(image);
    }
}
