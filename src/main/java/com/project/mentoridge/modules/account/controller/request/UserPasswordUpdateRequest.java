package com.project.mentoridge.modules.account.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPasswordUpdateRequest {

    // TODO - 입력값 체크
    /*
        - 비밀번호 제한
    */

    @ApiModelProperty(value = "현재 비밀번호", example = "password", required = true)
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String password;

    @ApiModelProperty(value = "새 비밀번호", example = "new_password", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, max = 14)
    private String newPassword;

    @ApiModelProperty(value = "새 비밀번호 확인", example = "new_password", required = true)
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    @Size(min = 6, max = 14)
    private String newPasswordConfirm;

    @Builder(access = AccessLevel.PRIVATE)
    private UserPasswordUpdateRequest(String password, String newPassword, String newPasswordConfirm) {
        this.password = password;
        this.newPassword = newPassword;
        this.newPasswordConfirm = newPasswordConfirm;
    }

    public static UserPasswordUpdateRequest of(String password, String newPassword, String newPasswordConfirm) {
        return UserPasswordUpdateRequest.builder()
                .password(password)
                .newPassword(newPassword)
                .newPasswordConfirm(newPasswordConfirm)
                .build();
    }

    @AssertTrue
    private boolean isNewPassword() {
        return !getPassword().equals(getNewPassword());
    }

    @AssertTrue
    private boolean isNewPasswordConfirm() {
        return getNewPasswordConfirm().equals(getNewPassword());
    }
}
