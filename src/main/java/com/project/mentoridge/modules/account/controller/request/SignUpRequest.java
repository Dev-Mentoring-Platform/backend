package com.project.mentoridge.modules.account.controller.request;

import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequest {

    // TODO - 입력값 체크
    /*
        - 비밀번호 제한
        - 연락처 정규식 체크
        - 이름 확인
        - 닉네임 확인 / 중복 체크
    */

    @ApiModelProperty(value = "아이디", example = "sh@email.com", required = true)
    @NotBlank(message = "이메일 형식의 아이디를 입력해주세요.")
    @Email
    private String username;

    @ApiModelProperty(value = "비밀번호", example = "password", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, max = 14)
    private String password;

    @ApiModelProperty(value = "비밀번호 확인", example = "password", required = true)
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    @Size(min = 6, max = 14)
    private String passwordConfirm;

    @ApiModelProperty(value = "이름", example = "sh", required = true)
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @ApiModelProperty(value = "성별", example = "MALE", required = false)
    private String gender;

//    @ApiModelProperty(value = "생년월일", example = "2020-01-01", required = false)
//    @Size(min = 10, max = 10)
//    private String birth;
    @ApiModelProperty(value = "출생년도", example = "1990", required = false)
    private String birthYear;

    @ApiModelProperty(value = "연락처", example = "01011112222", required = false)
    private String phoneNumber;

    @ApiModelProperty(value = "이메일", example = "sh@email.com", required = false)
    @Email
    private String email;

    @ApiModelProperty(value = "닉네임", example = "sh", required = true)
    @NotBlank
    private String nickname;

    @ApiModelProperty(value = "소개글", example = "안녕하세요", required = false)
    private String bio;

    @ApiModelProperty(value = "지역", example = "서울특별시 종로구 효자동", required = false)
    @NotBlank
    private String zone;

    private String image;

    @Builder(access = AccessLevel.PUBLIC)
    private SignUpRequest(String username, String password, String passwordConfirm, String name, String gender, String birthYear, String phoneNumber, String email, String nickname, String bio, String zone, String image) {
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.name = name;
        this.gender = gender;
        this.birthYear = birthYear;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nickname = nickname;
        this.bio = bio;
        this.zone = zone;
        this.image = image;
    }

    public User toEntity() {
        return User.builder()
                .username(username)
                .password(password)
                .name(name)
                .gender(gender)
                .birthYear(birthYear)
                .phoneNumber(phoneNumber)
                .email(email)
                .nickname(nickname)
                .bio(bio)
                .zone(zone)
                .image(image)
                .role(RoleType.MENTEE)
                .provider(null)
                .providerId(null)
                .build();
    }

    @AssertTrue
    private boolean isPasswordConfirm() {
        return getPasswordConfirm().equals(getPassword());
    }
}
