package com.project.mentoridge.modules.address.controller.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DongRequest {

    @NotBlank(message = "검색할 시/도를 입력해주세요.")
    private String state;

//    private String siGun;
//
//    private String gu;

    private String siGunGu;

    private DongRequest(@NotBlank(message = "검색할 시/도를 입력해주세요.") String state, String siGunGu) {
        this.state = state;
        this.siGunGu = siGunGu;
    }

    public static DongRequest of(String state, String siGunGu) {
        return new DongRequest(state, siGunGu);
    }

    // TODO - CHECK : -Valid
//    @AssertTrue(message = "검색할 시/군 혹은 구를 입력해주세요.")
//    private boolean isSiGunGuValid() {
//        if (StringUtils.isBlank(siGun) && StringUtils.isBlank(gu)) {
//            return false;
//        }
//        return true;
//    }
//    @AssertTrue(message = "검색할 시/군 혹은 구를 입력해주세요.")
//    private boolean isSiGunGuValid() {
//        if (StringUtils.isBlank(siGunGu)) {
//            return false;
//        }
//        return true;
//    }
}
