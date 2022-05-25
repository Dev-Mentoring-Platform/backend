package com.project.mentoridge.config.security.oauth;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.LoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final MenteeRepository menteeRepository;
    private final JwtTokenManager jwtTokenManager;
    private final LoginLogService loginLogService;

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
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        // super.onAuthenticationSuccess(request, response, authentication);
        log.info("oauth2 - onAuthenticationSuccess");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        OAuthAttributes attributes = OAuthAttributes.of(oAuth2User);
        String username = attributes.getEmail();

        // TODO - CHECK
        // 1. 카카오 로그인 VS 깃허브 로그인
        // 2. 닉네임 중복 처리
        // 3. 이메일(USERNAME) 중복 처리
        // 로그인
        User user = userRepository.findByProviderAndProviderId(attributes.getProvider(), attributes.getProviderId());
        if (user != null) {

            user.update(attributes.getName(), attributes.getPicture());
            JwtTokenManager.JwtResponse result = loginOAuth(username);
            try {
                // TODO - CHECK
//                response.setHeader(HEADER_ACCESS_TOKEN, result.getAccessToken());
//                response.setHeader(HEADER_REFRESH_TOKEN, result.getRefreshToken());
                String url = UriComponentsBuilder.fromUriString("http://13.125.235.217:3000/mentee")
                        .build().toUriString();
                getRedirectStrategy().sendRedirect(request, response, url);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            // 회원가입
            save(attributes);
            // TODO - CHECK : @RequestBody
            getRedirectStrategy().sendRedirect(request, response, "http://13.125.235.217:3000/");
        }
    }


    private User save(OAuthAttributes attributes) {

        String username = attributes.getEmail();
//        User user = userRepository.findByUsername(username)
//                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
//                .orElse(User.builder()
//                        .username(username)
//                        .password(username)
//                        .name(attributes.getName())
//                        .gender(null)
//                        .birthYear(null)
//                        .phoneNumber(null)
//                        .nickname(attributes.getName())
//                        .zone(null)
//                        .image(attributes.getPicture())
//                        .role(RoleType.MENTEE)
//                        .provider(attributes.getProvider())
//                        .providerId(attributes.getProviderId())
//                        .build());
        User user = User.builder()
                .username(username)
                .password(username)
                .name(attributes.getName())
                .gender(null)
                .birthYear(null)
                .phoneNumber(null)
                .nickname(attributes.getName())
                .zone(null)
                .image(attributes.getPicture())
                .role(RoleType.MENTEE)
                .provider(attributes.getProvider())
                .providerId(attributes.getProviderId())
                .build();
        // CascadeType.PERSIST로 중복 저장
        // User saved = userRepository.save(user);
        user.verifyEmail();

        Mentee mentee = Mentee.builder()
                .user(user)
                .build();
        menteeRepository.save(mentee);
        return user;
    }
}
