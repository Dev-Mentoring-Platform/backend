package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.security.oauth.OAuthAttributes;
import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class OAuthLoginServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    OAuthLoginService oAuthLoginService;

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;

    private OAuthAttributes oAuthAttributes;

    @BeforeEach
    @Override
    protected void init() {

        initDatabase();

        String registrationId = "Naver";
        String nameAttributeKey = "id";

        Map<String, Object> attributes = new HashMap<>();
            Map<String, Object> response = new HashMap<>();
            response.put("name", "user");
            response.put("email", "user@email.com");
            response.put("picture", null);
            response.put(nameAttributeKey, "providerId");
        attributes.put("response", response);
        oAuthAttributes = OAuthAttributes.of(registrationId, nameAttributeKey, attributes);
    }

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
            Map<String, Object> response = new HashMap<>();
            response.put("name", "user");
            response.put("email", "user@email.com");
            response.put("picture", null);
            response.put(nameAttributeKey, "providerId");
        attributes.put("response", response);
        oAuthAttributes = OAuthAttributes.of(registrationId, nameAttributeKey, attributes);
        assertThrows(RuntimeException.class,
                () -> oAuthLoginService.save(oAuthAttributes));
    }

    @Test
    void save() {

        // given
        // when
        User user = oAuthLoginService.save(oAuthAttributes);

        // then
        assertNotNull(user);
        assertAll(
                () -> assertTrue(user.isEmailVerified()),
                () -> assertEquals(oAuthAttributes.getAttributes().get("email"), user.getUsername()),
                () -> assertEquals(oAuthAttributes.getAttributes().get("name"), user.getNickname()),
                () -> assertEquals(oAuthAttributes.getAttributes().get("picture"), user.getImage())
        );
        assertNotNull(menteeRepository.findByUser(user));
    }

    @DisplayName("닉네임 (name)+1로 처리")
    @Test
    void save_when_existed_nickname() {
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
        User user = oAuthLoginService.save(oAuthAttributes);

        // then
        assertNotNull(user);
        assertAll(
                () -> assertTrue(user.isEmailVerified()),
                () -> assertEquals(oAuthAttributes.getAttributes().get("email"), user.getUsername()),
                () -> assertEquals(oAuthAttributes.getAttributes().get("name") + "2", user.getNickname()),
                () -> assertEquals(oAuthAttributes.getAttributes().get("picture"), user.getImage())
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
        User user = oAuthLoginService.save(oAuthAttributes);
        assertTrue(user.isEmailVerified());

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