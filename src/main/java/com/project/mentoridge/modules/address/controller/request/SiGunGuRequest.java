package com.project.mentoridge.modules.address.controller.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SiGunGuRequest {

    @NotBlank(message = "시/도를 입력해주세요.")
    private String state;

    @Builder(access = AccessLevel.PUBLIC)
    private SiGunGuRequest(String state) {
        this.state = state;
    }
}
