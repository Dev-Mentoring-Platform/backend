package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.SessionUser;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

@Slf4j
@Api(tags = {"LoginController"})
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
/*
    // 네이버 OAuth 로그인 필수 파라미터
    // https://developers.naver.com/docs/login/web/web.md
    // CSRF 방지를 위한 상태 토큰 생성
    // 추후 검증을 위해 세션에 저장된다.
    private String generateState() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    @ApiIgnore
    @GetMapping("/oauth/{provider}")
    public void oauth(@PathVariable(name = "provider") String provider, HttpServletRequest request, HttpServletResponse response) {

        try {
            // 로그인 요청 주소
            // 사용자가 동의하면 code를 callback
            String url = null;
            switch (provider) {
                case "google":
                    url = "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&redirect_uri=http://localhost:8080/oauth/google/callback&client_id=902783645965-ald60d1ehnaeaoetihtb1861u98ppf3u.apps.googleusercontent.com&scope=https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
                    break;
                case "kakao":
                    url = "https://kauth.kakao.com/oauth/authorize?client_id=8dc9eea7e202a581e0449058e753beaf&redirect_uri=http://localhost:8080/oauth/kakao/callback&response_type=code";
                    break;
                case "naver":
                    // 네이버 - state, error, error_description
                    String state = generateState();
                    request.getSession().setAttribute("state", state);
                    url = "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="
                            + "NNG0ZvRBJlxlE5DbApJR" + "&redirect_uri=" + URLEncoder.encode("http://localhost:8080/oauth/naver/callback", "UTF-8") + "&state=" + state;
                    break;
                default:
                    // TODO - 예외 처리
                    throw new RuntimeException("지원하지 않는 형식입니다.");
                    // break;
            }

            if (StringUtils.hasLength(url)) {
                response.sendRedirect(url);
            }

        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @ApiIgnore
    @GetMapping("/oauth/callback/{provider}")
    public ResponseEntity<?> oauthCallback(@PathVariable(name = "provider") String provider, @RequestBody AuthorizeResult authorizeResult) {

        Map<String, String> result = loginService.processLoginOAuth(provider, authorizeResult);
        return new ResponseEntity(getHeaders(result), HttpStatus.OK);
    }

    */
/**
     OAuth 로그인/회원가입
     *//*

    @ApiIgnore
    @GetMapping("/oauth/{provider}/callback")
    public ResponseEntity<?> oauth(@PathVariable(name = "provider") String provider,
                                   @RequestParam(name = "code", required = true) String code) {

        Map<String, String> result = loginService.oauth(provider, code);
        return new ResponseEntity(getHeaders(result), HttpStatus.OK);
    }
*/

    // TODO - TEST
    // 멘토/멘티 전환
    // @ApiIgnore
    @ApiOperation("멘토/멘티 전환")
    @GetMapping("/api/change-type")
    public SessionUser changeType() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // TODO - 제네릭 체크 (? extends GrantedAuthority - SimpleGrantedAuthority)
        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());

        String authority = authorities.get(0).getAuthority();
        authorities.clear();
        if (authority.equals(RoleType.MENTEE.getType())) {
            authorities.add(new SimpleGrantedAuthority(RoleType.MENTOR.getType()));
        } else if (authority.equals(RoleType.MENTOR.getType())) {
            authorities.add(new SimpleGrantedAuthority(RoleType.MENTEE.getType()));
        }

        Authentication updatedAuthentication
                = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities);
        SecurityContextHolder.getContext().setAuthentication(updatedAuthentication);

        // 세션 조회
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof PrincipalDetails) {

            PrincipalDetails principalDetails = (PrincipalDetails) principal;
            return new SessionUser(principalDetails);
        }
        return null;
    }

    // TODO - TEST
    @ApiOperation("세션 조회")
    @GetMapping("/api/session-user")
    public SessionUser getSessionUser() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof PrincipalDetails) {

            PrincipalDetails principalDetails = (PrincipalDetails) principal;
            return new SessionUser(principalDetails);
        }
        return null;
//        return loginService.getSessionUser();
    }

    @ApiOperation("OAuth 회원가입 추가 정보 입력")
    @PostMapping("/api/sign-up/oauth/detail")
    public ResponseEntity<?> signUpOAuthDetail(@CurrentUser User user,
                                               @Valid @RequestBody SignUpOAuthDetailRequest signUpOAuthDetailRequest) {

        loginService.signUpOAuthDetail(user, signUpOAuthDetailRequest);
        return ok();
    }

    @ApiOperation("일반 회원가입 - 기본 멘티로 가입")
    @PostMapping("/api/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {

        loginService.signUp(signUpRequest);
        return created();
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

    @ApiOperation("일반 로그인")
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {

        Map<String, String> result = loginService.login(loginRequest);
        // return new ResponseEntity(getHeaders(result), HttpStatus.OK);
        return ResponseEntity.ok(result.get("token"));
    }

    private HttpHeaders getHeaders(Map<String, String> result) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(result.get("header"), result.get("token"));
        return headers;
    }

    @ApiOperation("비밀번호 찾기")
    @GetMapping("/api/find-password")
    public ResponseEntity<?> findPassword(@RequestParam(name = "username") String username) {

        loginService.findPassword(username);
        return ok();
    }

}