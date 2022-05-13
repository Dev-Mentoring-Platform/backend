package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OAuthLoginServiceIntegrationTest {


    // TODO - TEST
    @Disabled
    @Test
    void 회원가입_OAuth() {

        // Given
        // When
        Map<String, Object> attributes = new HashMap<>();
        Map<String, String> result = null;
        // loginService.signUpOAuth(new GoogleInfo(attributes));

        // Then
        // 유저 생성 확인
        // 이메일 verify 확인
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertNotNull(user);
        assertTrue(user.isEmailVerified());
        System.out.println(String.format("provider : %s, providerId : %s", user.getProvider(), user.getProviderId()));

        // 멘티 생성 확인
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(mentee);

        // 로그인 확인 - jwt 토큰생성
        assertTrue(result.containsKey("header"));
        assertTrue(result.containsKey("token"));

    }
}