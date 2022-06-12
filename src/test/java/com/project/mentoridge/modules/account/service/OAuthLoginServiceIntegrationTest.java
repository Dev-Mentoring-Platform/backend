package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.security.oauth.OAuthAttributes;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class OAuthLoginServiceIntegrationTest {

    @Autowired
    OAuthLoginService oAuthLoginService;

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;


    @Test
    void save_when_existed_email() {

        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("user@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("user")
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber(null)
                .nickname("user")
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
        loginService.signUp(signUpRequest);
        // loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // when
        // then
        String registrationId = "Naver";
        String nameAttributeKey = "id";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "user");
        attributes.put("email", "user@email.com");
        attributes.put("picture", null);
        attributes.put(nameAttributeKey, "providerId");
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, nameAttributeKey, attributes);
        assertThrows(RuntimeException.class,
                () -> oAuthLoginService.save(oAuthAttributes));
    }

    @Test
    void save() {

        // given
        // when
        String registrationId = "Naver";
        String nameAttributeKey = "id";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "user");
        attributes.put("email", "user@email.com");
        attributes.put("picture", null);
        attributes.put(nameAttributeKey, "providerId");
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, nameAttributeKey, attributes);
        User user = oAuthLoginService.save(oAuthAttributes);

        // then
        assertNotNull(user);
        assertAll(
                () -> assertTrue(user.isEmailVerified()),
                () -> assertEquals(attributes.get("email"), user.getUsername()),
                () -> assertEquals(attributes.get("name"), user.getNickname()),
                () -> assertEquals(attributes.get("picture"), user.getImage())
        );
        assertNotNull(menteeRepository.findByUser(user));
    }

    @Test
    void save_when_existed_nickname() {
        // 닉네임(name) +1로 임시 처리

        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("user@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("user")
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber(null)
                .nickname("user")
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
        loginService.signUp(signUpRequest);
        // loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // when
        String registrationId = "Naver";
        String nameAttributeKey = "id";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "user");
        attributes.put("email", "oauth2User@email.com");
        attributes.put("picture", null);
        attributes.put(nameAttributeKey, "providerId");
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, nameAttributeKey, attributes);
        User user = oAuthLoginService.save(oAuthAttributes);

        // then
        assertNotNull(user);
        assertAll(
                () -> assertTrue(user.isEmailVerified()),
                () -> assertEquals(attributes.get("email"), user.getUsername()),
                () -> assertEquals(attributes.get("name") + "1", user.getNickname()),
                () -> assertEquals(attributes.get("picture"), user.getImage())
        );
        assertNotNull(menteeRepository.findByUser(user));
    }

    @DisplayName("회원 정보 추가 입력 - OAuth 가입이 아닌 경우")
    @Test
    void oauth_detail_when_not_oauth_user() {

        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("user@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("user")
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber(null)
                .nickname("user")
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
        User user = loginService.signUp(signUpRequest);
        // loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // when
        // then
        SignUpOAuthDetailRequest signUpOAuthDetailRequest = SignUpOAuthDetailRequest.builder()
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber("01012345678")
                .nickname("nickname")
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
        assertThrows(RuntimeException.class, () -> {
            oAuthLoginService.signUpOAuthDetail(user, signUpOAuthDetailRequest);
        });
    }

    @Test
    void oauth_detail_existed_nickname() {

        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("user@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("user")
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber(null)
                .nickname("user")
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
        User other = loginService.signUp(signUpRequest);
        // loginService.verifyEmail(other.getUsername(), other.getEmailVerifyToken());

        String registrationId = "Naver";
        String nameAttributeKey = "id";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "user");
        attributes.put("email", "oauth2User@email.com");
        attributes.put("picture", null);
        attributes.put(nameAttributeKey, "providerId");
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, nameAttributeKey, attributes);
        User user = oAuthLoginService.save(oAuthAttributes);
        assertTrue(user.isEmailVerified());
        assertEquals("user2", user.getNickname());

        // when
        // then
        SignUpOAuthDetailRequest signUpOAuthDetailRequest = SignUpOAuthDetailRequest.builder()
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber("01012345678")
                .nickname("user")
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
        assertThrows(AlreadyExistException.class, () -> {
            oAuthLoginService.signUpOAuthDetail(user, signUpOAuthDetailRequest);
        });
    }

    @Test
    void oauth_detail() {

        // given
        // when
        String registrationId = "Naver";
        String nameAttributeKey = "id";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "user");
        attributes.put("email", "oauth2User@email.com");
        attributes.put("picture", null);
        attributes.put(nameAttributeKey, "providerId");
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, nameAttributeKey, attributes);
        User user = oAuthLoginService.save(oAuthAttributes);
        assertTrue(user.isEmailVerified());
        assertEquals("user2", user.getNickname());

        // when
        SignUpOAuthDetailRequest signUpOAuthDetailRequest = SignUpOAuthDetailRequest.builder()
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber("01012345678")
                .nickname("nickname")
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build();
        oAuthLoginService.signUpOAuthDetail(user, signUpOAuthDetailRequest);

        // then
        assertAll(
                () -> assertEquals(signUpOAuthDetailRequest.getGender(), user.getGender()),
                () -> assertEquals(signUpOAuthDetailRequest.getBirthYear(), user.getBirthYear()),
                () -> assertEquals(signUpOAuthDetailRequest.getPhoneNumber(), user.getPhoneNumber()),
                () -> assertEquals(signUpOAuthDetailRequest.getNickname(), user.getNickname()),
                () -> assertEquals(signUpOAuthDetailRequest.getZone(), user.getZone().toString()),
                () -> assertEquals(signUpOAuthDetailRequest.getImage(), user.getImage())
        );
    }
}