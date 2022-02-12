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
public class LoginRequest {

    @ApiModelProperty(value = "아이디", example = "yk@email.com", required = true)
    @Email @NotBlank(message = "이메일 형식의 아이디를 입력해주세요.")
    private String username;

    @ApiModelProperty(value = "비밀번호", example = "password", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Builder(access = AccessLevel.PRIVATE)
    private LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static LoginRequest of(String username, String password) {
        return LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
    }
}
