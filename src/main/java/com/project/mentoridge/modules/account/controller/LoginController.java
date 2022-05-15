package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.SessionUser;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

//@CrossOrigin(origins = "http://13.125.235.217:3000", allowCredentials = "true")
@Slf4j
@Api(tags = {"LoginController"})
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    // 멘토/멘티 전환
    // @ApiIgnore
    @ApiOperation("멘토/멘티 전환")
    @GetMapping("/api/change-type")
    public ResponseEntity<?> changeType(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Map<String, String> result = loginService.changeType(principalDetails.getUsername(), principalDetails.getAuthority());
        return ResponseEntity.ok(result.get("token"));
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
                                         @RequestParam(name = "token") String token) {

        log.info("email : {}, token : {}", email, token);
        loginService.verifyEmail(email, token);
        return ok();
    }

    @ApiOperation("비밀번호 찾기")
    @GetMapping("/api/find-password")
    public ResponseEntity<?> findPassword(@RequestParam(name = "username") String username) {

        loginService.findPassword(username);
        return ok();
    }

    @ApiOperation("일반 로그인")
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        Map<String, String> result = loginService.login(loginRequest);
        // return new ResponseEntity(getHeaders(result), HttpStatus.OK);

        String token = result.get("token");
        String[] split = token.split(" ");
        response.setHeader("X-Auth-Token", split[1]);

//        ResponseCookie cookie = ResponseCookie.from("X-Auth-Token", split[1])
//                .path("/")
//                .sameSite("")
//                //.secure(true)
//                .build();
//        response.addHeader("Set-Cookie", cookie.toString());
        Cookie cookie = new Cookie("X-AUTH-TOKEN", split[1]);
        cookie.setPath("/");
        // cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(token);
    }

        private HttpHeaders getHeaders(Map<String, String> result) {
            HttpHeaders headers = new HttpHeaders();
            headers.set(result.get("header"), result.get("token"));
            return headers;
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