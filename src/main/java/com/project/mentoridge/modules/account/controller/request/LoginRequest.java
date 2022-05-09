package com.project.mentoridge.modules.account.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {

    @ApiModelProperty(value = "아이디", example = "user103@email.com", required = true)
    @Email @NotBlank(message = "이메일 형식의 아이디를 입력해주세요.")
    private String username;

    @ApiModelProperty(value = "비밀번호", example = "password", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Builder(access = AccessLevel.PUBLIC)
    private LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
