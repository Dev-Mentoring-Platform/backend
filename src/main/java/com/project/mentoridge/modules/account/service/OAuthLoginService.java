package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.config.security.oauth.OAuthAttributes;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.LoginLogService;
import com.project.mentoridge.modules.log.component.MenteeLogService;
import com.project.mentoridge.modules.log.component.UserLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.exception.AlreadyExistException.NICKNAME;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.USER;
import static com.project.mentoridge.modules.account.service.LoginService.getMenteeClaims;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuthLoginService {

    private final UserRepository userRepository;
    private final MenteeRepository menteeRepository;
    private final JwtTokenManager jwtTokenManager;

    private final LoginLogService loginLogService;
    private final UserLogService userLogService;
    private final MenteeLogService menteeLogService;

    public JwtTokenManager.JwtResponse loginOAuth(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("username : " + username));

        // accessToken
        String accessToken = jwtTokenManager.createToken(username, getMenteeClaims(username));
        // refreshToken
        String refreshToken = jwtTokenManager.createRefreshToken();
        user.updateRefreshToken(refreshToken);

        // lastLoginAt
        user.login(loginLogService);
        return jwtTokenManager.getJwtTokens(accessToken, refreshToken);
    }

    public User save(OAuthAttributes attributes) {

        String username = attributes.getEmail();
        // TODO - ????????? ?????? ??????
        if (checkUsernameDuplication(username)) {
            throw new RuntimeException("?????? ???????????? ???????????????.");
        }

        // ????????? ?????? ??????
        String name = attributes.getName();
        int count = userRepository.countAllByNickname(name);
        String nickname = count == 0 ? name : name + (count + 1);
        User user = User.builder()
                .username(username)
                .password(username)
                .name(name)
                .gender(null)
                .birthYear(null)
                .phoneNumber(null)
                .nickname(nickname)
                .zone(null)
                .image(attributes.getPicture())
                .role(RoleType.MENTEE)
                .provider(attributes.getProvider())
                .providerId(attributes.getProviderId())
                .build();
        // CascadeType.PERSIST??? ?????? ??????
        // User saved = userRepository.save(user);
        Mentee saved = menteeRepository.save(Mentee.builder()
                .user(user)
                .build());
        menteeLogService.insert(user, saved);
        user.verifyEmail(userLogService);
        return user;
    }

    public void signUpOAuthDetail(User user, SignUpOAuthDetailRequest signUpOAuthDetailRequest) {

        user = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(USER));

        // TODO - ?????? : OAuth??? ????????? ????????? ?????? ??????
        if (user.getProvider() == null || StringUtils.isBlank(user.getProviderId())) {
            throw new RuntimeException("OAuth??? ????????? ????????? ????????????.");
        }
        if (checkNicknameDuplication(signUpOAuthDetailRequest.getNickname())) {
            throw new AlreadyExistException(NICKNAME);
        }
        user.updateOAuthDetail(signUpOAuthDetailRequest, userLogService);
    }

    private boolean checkUsernameDuplication(String username) {
        boolean duplicated = false;

        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException();
        }

        User user = userRepository.findAllByUsername(username);
        if (user != null) {
            duplicated = true;
        }
        return duplicated;
    }

    private boolean checkNicknameDuplication(String nickname) {
        boolean duplicated = false;

        if (StringUtils.isBlank(nickname)) {
            throw new IllegalArgumentException();
        }

        User user = userRepository.findAllByNickname(nickname);
        if (user != null) {
            duplicated = true;
        }

        return duplicated;
    }
/*
    public Map<String, String> processLoginOAuth(String provider, AuthorizeResult authorizeResult) {

        OAuthInfo oAuthInfo = getOAuthInfo(provider, authorizeResult);
        if (oAuthInfo != null) {

            User user = userRepository.findByProviderAndProviderId(oAuthInfo.getProvider(), oAuthInfo.getProviderId());
            if (user != null) {
                // ?????????
                return loginOAuth(user);
            }
        }

        return null;
    }

    public Map<String, String> oauth(String provider, String code) {

        Map<String, String> result = null;

        OAuthInfo oAuthInfo = getOAuthInfo(provider, code);
        if (oAuthInfo != null) {

            User user = userRepository.findByProviderAndProviderId(oAuthInfo.getProvider(), oAuthInfo.getProviderId());
            if (user != null) {
                // ?????? ????????? ??????????????? ?????? ????????? ??????
                result = loginOAuth(user);
            } else {
                // ???????????? ??? ?????? ?????????
                // ?????? ?????? ??????
                // ?????? ?????? ?????? ??????
                result = signUpOAuth(oAuthInfo);
            }
        }

        log.info("#oauth-result : " + result);
        return result;
    }

    public OAuthInfo getOAuthInfo(String provider, AuthorizeResult authorizeResult) {

        OAuthInfo oAuthInfo = null;

        OAuthType oAuthType = OAuthInfo.getOAuthType(provider);
        if (oAuthType == null) {
            throw new OAuthAuthenticationException(UNSUPPORTED);
        }

        switch (oAuthType) {
            case GOOGLE:
                // authorizeResult.getUser() -> Map<String, String>
                Map<String, String> googleOAuthUserInfo = googleOAuth.getUserInfo(authorizeResult.getUser());
                if (googleOAuthUserInfo != null) {
                    oAuthInfo = new GoogleInfo(googleOAuthUserInfo);
                }
                break;
            case KAKAO:
                break;
            case NAVER:
                break;
            default:
                throw new OAuthAuthenticationException(UNSUPPORTED);
        }

        if (oAuthInfo == null) {
            throw new OAuthAuthenticationException(UNPARSABLE);
        }

        return oAuthInfo;
    }

    public OAuthInfo getOAuthInfo(String provider, String code) {

        OAuthInfo oAuthInfo = null;

        OAuthType oAuthType = OAuthInfo.getOAuthType(provider);
        if (oAuthType == null) {
            throw new OAuthAuthenticationException(UNSUPPORTED);
        }

        switch (oAuthType) {
            case GOOGLE:
                Map<String, String> googleOAuthUserInfo = googleOAuth.getUserInfo(code);
                if (googleOAuthUserInfo != null) {
                    oAuthInfo = new GoogleInfo(googleOAuthUserInfo);
                }
                break;
            case KAKAO:
                KakaoResponse kakaoOAuthUserInfo = kakaoOAuth.getUserInfo(code);
                if (kakaoOAuthUserInfo != null) {
                    oAuthInfo = new KakaoInfo(kakaoOAuthUserInfo);
                }
                break;
            case NAVER:
                NaverResponse naverOAuthUserInfo = naverOAuth.getUserInfo(code);
                if (naverOAuthUserInfo != null) {
                    oAuthInfo = new NaverInfo(naverOAuthUserInfo);
                }
                break;
            default:
                throw new OAuthAuthenticationException(UNSUPPORTED);
        }

        if (oAuthInfo == null) {
            throw new OAuthAuthenticationException(UNPARSABLE);
        }

        return oAuthInfo;
    }

    public Map<String, String> signUpOAuth(OAuthInfo oAuthInfo) {

        String username = oAuthInfo.getEmail();
        if (checkUsernameDuplication(username)) {
            throw new AlreadyExistException(ID);
        }

        User user = User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(username))
                .name(oAuthInfo.getName())
                .gender(null)
                .birthYear(null)
                .phoneNumber(null)
                .nickname(username)
                .zone(null)
                .image(null)
                .role(RoleType.MENTEE)
                .provider(oAuthInfo.getProvider())
                .providerId(oAuthInfo.getProviderId())
                .build();
        // ?????? ??????
        user.verifyEmail();

        Mentee mentee = Mentee.builder()
                .user(user)
                .build();
        menteeRepository.save(mentee);
        // ?????? ?????????
        return loginOAuth(user);
    }

    public Map<String, String> loginOAuth(User user) {
        String username = user.getUsername();
        return login(username, username);
    }
*/

}
