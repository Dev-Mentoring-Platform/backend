package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.modules.account.service.LoginService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = {"OAuthLoginController"})
@RestController
@RequiredArgsConstructor
public class OAuthLoginController {

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

}