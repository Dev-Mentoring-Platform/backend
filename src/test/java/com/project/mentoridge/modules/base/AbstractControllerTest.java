package com.project.mentoridge.modules.base;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.PrincipalDetailsService;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.config.security.oauth.CustomOAuth2SuccessHandler;
import com.project.mentoridge.config.security.oauth.CustomOAuth2UserService;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.TOKEN_PREFIX;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    protected static ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);;

    @MockBean
    protected JwtTokenManager jwtTokenManager;
    @MockBean
    protected PrincipalDetailsService principalDetailsService;
    @MockBean
    CustomOAuth2UserService customOAuth2UserService;
    @MockBean
    CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    protected User user;
    protected PrincipalDetails principalDetails;
    protected String accessToken;
    protected String accessTokenWithPrefix;

    public String createAccessToken(String username, RoleType roleType, boolean expired) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", roleType.getType());
        return createAccessToken(username, claims, expired);
    }

    private String createAccessToken(String subject, Map<String, Object> claims, boolean expired) {
        LocalDateTime now = LocalDateTime.now();
        Timestamp issuedAt = Timestamp.valueOf(now);
        Timestamp expiredAt = null;
        if (expired) {
            expiredAt = Timestamp.valueOf(now.minusSeconds(86400));
        } else {
            expiredAt = Timestamp.valueOf(now.plusSeconds(86400));
        }
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiredAt)
                .withPayload(claims)
                .sign(Algorithm.HMAC256("test"));
    }

    private String createRefreshToken(boolean expired) {
        LocalDateTime now = LocalDateTime.now();
        Timestamp issuedAt = Timestamp.valueOf(now);

        Timestamp expiredAt = null;
        if (expired) {
            expiredAt = Timestamp.valueOf(now.minusSeconds(86400));
        } else {
            expiredAt = Timestamp.valueOf(now.plusSeconds(86400));
        }
        return JWT.create()
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiredAt)
                .sign(Algorithm.HMAC256("test"));
    }

    @BeforeEach
    protected void init() {

        user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");

        accessToken = createAccessToken("user@email.com", RoleType.MENTEE, false);
        accessTokenWithPrefix = TOKEN_PREFIX + accessToken;

        when(jwtTokenManager.verifyToken(accessToken)).thenReturn(true);
        when(jwtTokenManager.getClaim(accessToken, "username")).thenReturn("user@email.com");
        when(jwtTokenManager.getClaim(accessToken, "role")).thenReturn(RoleType.MENTOR.getType());

        principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUser()).thenReturn(user);
        when(principalDetailsService.loadUserByUsername("user@email.com")).thenReturn(principalDetails);
    }
}
