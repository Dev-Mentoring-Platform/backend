package com.project.mentoridge.modules.address.controller.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SiGunGuRequest {

    @NotBlank(message = "시/도를 입력해주세요.")
    private String state;

    private SiGunGuRequest(@NotBlank(message = "시/도를 입력해주세요.") String state) {
        this.state = state;
    }

    public static SiGunGuRequest of(String state) {
        return new SiGunGuRequest(state);
    }
}
