package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.SessionUser;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.OAuthLoginService;
import com.project.mentoridge.modules.account.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.HEADER_ACCESS_TOKEN;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.HEADER_REFRESH_TOKEN;

@Slf4j
@Api(tags = {"LoginController"})
@RestController
@RequiredArgsConstructor
public class LoginController {

    @Value("${mentoridge-config.url}")
    private String url;

    private final LoginService loginService;
    private final OAuthLoginService oAuthLoginService;
/*
    @Value("${front.ip}")
    private String frontIp;
    @Value("${front.port}")
    private Integer frontPort;*/

    // 멘토/멘티 전환
    // @ApiIgnore
    @ApiOperation("멘토/멘티 전환")
    @GetMapping("/api/change-type")
    public ResponseEntity<?> changeType(@AuthenticationPrincipal PrincipalDetails principalDetails, HttpServletResponse response) {
        JwtTokenManager.JwtResponse result = loginService.changeType(principalDetails.getUsername(), principalDetails.getAuthority());
        String accessToken = result.getAccessToken();
        String refreshToken = result.getRefreshToken();
        response.setHeader(HEADER_ACCESS_TOKEN, accessToken);
        response.setHeader(HEADER_REFRESH_TOKEN, refreshToken);

        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("멘토 전환 가능여부 확인")
    @GetMapping("/api/check-role")
    public ResponseEntity<?> checkRole(@AuthenticationPrincipal PrincipalDetails principalDetails, HttpServletResponse response) {
        return ResponseEntity.ok(principalDetails.getUser().getRole().equals(RoleType.MENTOR));
    }

    @ApiOperation("세션 조회")
    @GetMapping("/api/session-user")
    public ResponseEntity<?> getSessionUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        SessionUser user = new SessionUser(principalDetails);
        return ResponseEntity.ok(user);
    }

    @ApiOperation("일반 회원가입 - 기본 멘티로 가입")
    @PostMapping("/api/sign-up")
    public ResponseEntity<?> signUp(@Validated @RequestBody SignUpRequest signUpRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        loginService.signUp(signUpRequest);
        return created();
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("OAuth 회원가입 추가 정보 입력")
    @PostMapping("/api/sign-up/oauth/detail")
    public ResponseEntity<?> signUpOAuthDetail(@CurrentUser User user,
                                               @Validated @RequestBody SignUpOAuthDetailRequest signUpOAuthDetailRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        oAuthLoginService.signUpOAuthDetail(user, signUpOAuthDetailRequest);
        return ok();
    }

    // 계정 인증
    @ApiIgnore
    @GetMapping("/api/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam(name = "email") String email,
                                         @RequestParam(name = "token") String token,
                                         HttpServletResponse response) {

        log.info("email : {}, token : {}", email, token);
        loginService.verifyEmail(email, token);

        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok();
    }

    @ApiOperation("비밀번호 찾기")
    @GetMapping("/api/find-password")
    public ResponseEntity<?> findPassword(@RequestParam(name = "username") String username) {

        loginService.findPassword(username);
        return ok();
    }
/*
    @ApiOperation("OAuth 로그인")
    @GetMapping("/api/login-oauth")
    public ResponseEntity<?> loginOAuth(@Valid @RequestParam(name = "username") String username, HttpServletResponse response) {

        JwtTokenManager.JwtResponse result = loginService.loginOAuth(username);
        String accessToken = result.getAccessToken();
        String refreshToken = result.getRefreshToken();
        response.setHeader(HEADER_ACCESS_TOKEN, accessToken);
        response.setHeader(HEADER_REFRESH_TOKEN, refreshToken);

        return ResponseEntity.ok(result);
    }*/

    @ApiOperation("일반 로그인")
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginRequest, BindingResult bindingResult, HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        JwtTokenManager.JwtResponse result = loginService.login(loginRequest);
        if (result != null) {

            String accessTokenWithPrefix = result.getAccessToken();
            String refreshTokenWithPrefix = result.getRefreshToken();
            response.setHeader(HEADER_ACCESS_TOKEN, accessTokenWithPrefix);
            response.setHeader(HEADER_REFRESH_TOKEN, refreshTokenWithPrefix);
        }
        return ResponseEntity.ok(result);
    }

    @ApiOperation("Refresh Token")
    @PostMapping("/api/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader(name = HEADER_ACCESS_TOKEN) String accessTokenWithPrefix,
                                          @RequestHeader(name = HEADER_REFRESH_TOKEN) String refreshTokenWithPrefix,
                                          @RequestHeader(name = "role") String role,
                                          HttpServletResponse response) {

        JwtTokenManager.JwtResponse result = loginService.refreshToken(accessTokenWithPrefix, refreshTokenWithPrefix, role);
        if (result != null) {
            String newAccessTokenWithPrefix = result.getAccessToken();
            String newRefreshTokenWithPrefix = result.getRefreshToken();
            response.setHeader(HEADER_ACCESS_TOKEN, newAccessTokenWithPrefix);
            response.setHeader(HEADER_REFRESH_TOKEN, newRefreshTokenWithPrefix);
        }
        return ResponseEntity.ok(result);
    }

    @ApiOperation("아이디 중복체크")
    @GetMapping("/api/check-username")
    public boolean checkUsername(@RequestParam(required = true) String username) {
        return loginService.checkUsernameDuplication(username);
    }

    @ApiOperation("닉네임 중복체크")
    @GetMapping("/api/check-nickname")
    public boolean checkNickname(@RequestParam String nickname) {
        return loginService.checkNicknameDuplication(nickname);
    }

}