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
import com.project.mentoridge.modules.account.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

    private final LoginService loginService;
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
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {

        loginService.signUp(signUpRequest);
        return created();
    }

    @PreAuthorize("hasRole('ROLE_MENTEE')")
    @ApiOperation("OAuth 회원가입 추가 정보 입력")
    @PostMapping("/api/sign-up/oauth/detail")
    public ResponseEntity<?> signUpOAuthDetail(@CurrentUser User user,
                                               @Valid @RequestBody SignUpOAuthDetailRequest signUpOAuthDetailRequest) {
        loginService.signUpOAuthDetail(user, signUpOAuthDetailRequest);
        return ok();
    }

    // 계정 인증
    // TODO - CHECK : GET vs POST
    @ApiIgnore
    @GetMapping("/api/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam(name = "email") String email,
                                         @RequestParam(name = "token") String token,
                                         HttpServletResponse response) {

        log.info("email : {}, token : {}", email, token);
        loginService.verifyEmail(email, token);

        try {
            response.sendRedirect("http://localhost:3000/");
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        JwtTokenManager.JwtResponse result = loginService.login(loginRequest);
        String accessToken = result.getAccessToken();
        String refreshToken = result.getRefreshToken();
        response.setHeader(HEADER_ACCESS_TOKEN, accessToken);
        response.setHeader(HEADER_REFRESH_TOKEN, refreshToken);
//
//        ResponseCookie cookie = ResponseCookie.from(HEADER_ACCESS_TOKEN, accessToken)
//                .httpOnly(true)
//                .sameSite("None")
//                .secure(true)
//                //.domain("mentoridge.co.kr")
//                .path("/")
//                .maxAge(60 * 60 * 24)
//                .build();
//        response.addHeader("Set-Cookie", cookie.toString());
//        Cookie cookie = new Cookie(HEADER_ACCESS_TOKEN, accessToken);
//        cookie.setPath("/");
//        cookie.setDomain("mentoridge.co.kr");
//        cookie.setHttpOnly(true);
//        response.addCookie(cookie);

        return ResponseEntity.ok(result);
    }

    @ApiOperation("Refresh Token")
    @PostMapping("/api/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader(name = HEADER_ACCESS_TOKEN) String accessToken,
                                          @RequestHeader(name = HEADER_REFRESH_TOKEN) String refreshToken,
                                          @RequestHeader(name = "role") String role,
                                          HttpServletResponse response) {

        JwtTokenManager.JwtResponse result = loginService.refreshToken(accessToken, refreshToken, role);
        String _accessToken = result.getAccessToken();
        String _refreshToken = result.getRefreshToken();
        response.setHeader(HEADER_ACCESS_TOKEN, _accessToken);
        response.setHeader(HEADER_REFRESH_TOKEN, _refreshToken);
/*
        ResponseCookie cookie = ResponseCookie.from(HEADER_ACCESS_TOKEN, _accessToken)
                .path("/")
                .sameSite("")
                .domain("mentoridge.co.kr")
                .httpOnly(true)
                //.secure(true)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());*/
        return ResponseEntity.ok(result);
    }

    @ApiOperation("아이디 중복체크")
    @GetMapping("/api/check-username")
    public boolean checkUsername(@RequestParam String username) {
        return loginService.checkUsernameDuplication(username);
    }

    @ApiOperation("닉네임 중복체크")
    @GetMapping("/api/check-nickname")
    public boolean checkNickname(@RequestParam String nickname) {
        return loginService.checkNicknameDuplication(nickname);
    }

}