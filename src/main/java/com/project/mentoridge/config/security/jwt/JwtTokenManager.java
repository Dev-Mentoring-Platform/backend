package com.project.mentoridge.config.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.mentoridge.config.security.PrincipalDetails;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenManager {

    public JwtTokenManager(@Value("${jwt.secret}") String secret,
                           // 900초 = 15분
                           @Value("${jwt.token-validity-in-seconds}") long expiredAfter) {
        this.secret = secret;
        this.expiredAfter = expiredAfter;
        // refresh-token-validity-in-seconds
        this.refreshTokenExpiredAfter = 60 * 60 * 24;
    }

    public static final String TYPE_BEARER = "Bearer";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    public static final String HEADER_ACCESS_TOKEN = "X-Access-Token";
    public static final String HEADER_REFRESH_TOKEN = "X-Refresh-Token";

    private final String secret;
    private final long expiredAfter;
    private final long refreshTokenExpiredAfter;

    @Getter
    public static class JwtResponse {

        private String type = TYPE_BEARER;
        private String accessToken;
        private String refreshToken;

        public JwtResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    public String createToken(String subject, Map<String, Object> claims) {
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + expiredAfter))
                .withPayload(claims)
                .sign(Algorithm.HMAC256(secret));
    }

    // Refresh Token 생성
    public String createRefreshToken() {
        return JWT.create()
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpiredAfter))
                .sign(Algorithm.HMAC256(secret));
    }
/*
    public Map<String, String> convertTokenToMap(String accessToken) {
        Map<String, String> map = new HashMap<>();
        map.put("header", HEADER);
        map.put("token", TOKEN_PREFIX + accessToken);

        return map;
    }
    public Map<String, String> convertTokenToMap(String accessToken, String refreshToken) {
        Map<String, String> map = new HashMap<>();
        map.put("header", HEADER);
        map.put("access-token", TOKEN_PREFIX + accessToken);
        map.put("refresh-token", TOKEN_PREFIX + refreshToken);
        return map;
    }*/
    public JwtResponse getJwtTokens(String accessToken, String refreshToken) {
        return new JwtResponse(accessToken, refreshToken);
    }

    public DecodedJWT getDecodedToken(String accessToken) {
        if (accessToken == null || accessToken.length() == 0) {
            return null;
        }
        return JWT.require(Algorithm.HMAC256(secret)).build().verify(accessToken);
    }

    private boolean isExpiredToken(String accessToken) {
        return getDecodedToken(accessToken).getExpiresAt().before(new Date());
    }

    private Map<String, Claim> getClaims(String accessToken) {
        return getDecodedToken(accessToken).getClaims();
    }

    public String getClaim(String accessToken, String name) {
        return getClaims(accessToken).get(name).asString();
    }

    public boolean verifyToken(String accessToken, PrincipalDetails principalDetails) {
        boolean result = false;
        if (accessToken == null || accessToken.length() == 0) {
            return result;
        }
        return !isExpiredToken(accessToken) && (getClaim(accessToken, "username").equals(principalDetails.getUsername()));
    }
}
