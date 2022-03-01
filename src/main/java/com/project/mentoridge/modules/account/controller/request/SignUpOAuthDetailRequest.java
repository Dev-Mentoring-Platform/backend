package com.project.mentoridge.modules.account.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpOAuthDetailRequest {

    @ApiModelProperty(value = "성별", example = "MALE", required = false)
    private String gender;

    @ApiModelProperty(value = "출생년도", example = "1990", required = false)
    private String birthYear;

    @ApiModelProperty(value = "연락처", example = "010-1234-5678", required = false)
    private String phoneNumber;

    @ApiModelProperty(value = "닉네임", example = "sj", required = true)
    @NotBlank
    private String nickname;

    @ApiModelProperty(value = "지역", example = "서울특별시 종로구 효자동", required = false)
    @NotBlank
    private String zone;

    private String image;

    @Builder(access = AccessLevel.PUBLIC)
    private SignUpOAuthDetailRequest(String gender, String birthYear, String phoneNumber, String nickname, String zone, String image) {
        this.gender = gender;
        this.birthYear = birthYear;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.zone = zone;
        this.image = image;
    }

}
