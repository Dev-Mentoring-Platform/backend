package com.project.mentoridge.config.security.oauth;

import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.OAuthLoginService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.UserLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.HEADER_ACCESS_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    // TODO - HttpCookieOAuth2AuthorizationRequestRepository
    @Value("${mentoridge-config.url}")
    private String url;

    private final OAuthLoginService oAuthLoginService;
    private final UserRepository userRepository;
    private final UserLogService userLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        // super.onAuthenticationSuccess(request, response, authentication);
        log.info("oauth2 - onAuthenticationSuccess");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        OAuthAttributes attributes = OAuthAttributes.of(oAuth2User);

        // TODO - CHECK : 카카오 로그인 VS 깃허브 로그인
        // 로그인
        String username = attributes.getEmail();
        User user = userRepository.findByProviderAndProviderId(attributes.getProvider(), attributes.getProviderId());
        if (user != null) {

            user.update(attributes, userLogService);
            // TODO : CHECK - 트랜잭션
            JwtTokenManager.JwtResponse result = oAuthLoginService.loginOAuth(username);
            try {
//            response.setHeader(HEADER_ACCESS_TOKEN, result.getAccessToken());
//            response.setHeader(HEADER_REFRESH_TOKEN, result.getRefreshToken());
                String redirectUrl = UriComponentsBuilder.fromUriString(url)
                        .queryParam(HEADER_ACCESS_TOKEN, result.getAccessToken())
                        .build().toUriString();
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            // 회원가입
            oAuthLoginService.save(attributes);
            // TODO - CHECK : @RequestBody
            getRedirectStrategy().sendRedirect(request, response, url);
        }
    }

/*
    public JwtTokenManager.JwtResponse loginOAuth(String username) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", RoleType.MENTEE.getType());
        String accessToken = jwtTokenManager.createToken(username, claims);

        // lastLoginAt
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("username : " + username));
        userRepository.updateLastLoginAt(username);

        // refreshToken
        String refreshToken = jwtTokenManager.createRefreshToken();
        userRepository.updateRefreshToken(refreshToken, username);

        loginLogService.login(user);
        return jwtTokenManager.getJwtTokens(accessToken, refreshToken);
    }*/

}
