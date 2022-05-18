package com.project.mentoridge.config.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenManager {

    public JwtTokenManager(@Value("${jwt.secret}") String secret,
                           // 900초 = 15분
                           @Value("${jwt.token-validity-in-seconds}") long expiredAfter) {
        this.secret = secret;
        this.expiredAfter = 60 * 60 * 24;
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
            this.accessToken = TOKEN_PREFIX + accessToken;
            this.refreshToken = TOKEN_PREFIX + refreshToken;
        }
    }

    public String createToken(String subject, Map<String, Object> claims) {
        LocalDateTime now = LocalDateTime.now();
        Timestamp issuedAt = Timestamp.valueOf(now);
        Timestamp expiredAt = Timestamp.valueOf(now.plusSeconds(expiredAfter));
        log.info("access-token expires at " + expiredAt);
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiredAt)
                .withPayload(claims)
                .sign(Algorithm.HMAC256(secret));
    }

    // Refresh Token 생성
    public String createRefreshToken() {
        LocalDateTime now = LocalDateTime.now();
        Timestamp issuedAt = Timestamp.valueOf(now);
        Timestamp expiredAt = Timestamp.valueOf(now.plusSeconds(refreshTokenExpiredAfter));
        return JWT.create()
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiredAt)
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

    public DecodedJWT getDecodedToken(String token) {
        if (token == null || token.length() == 0) {
            return null;
        }
        return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
    }

    // TODO - CHECK
    private boolean isExpiredToken(String token) {
        // TokenExpiredException – if the token has expired.
        // return getDecodedToken(token).getExpiresAt().before(Timestamp.valueOf(LocalDateTime.now()));
        boolean result = false;
        try {
            result = getDecodedToken(token).getExpiresAt().before(Timestamp.valueOf(LocalDateTime.now()));
        } catch (TokenExpiredException e) {
            e.printStackTrace();
            result = true;
        }
        return result;
    }

    private Map<String, Claim> getClaims(String accessToken) {
        return getDecodedToken(accessToken).getClaims();
    }

    public String getClaim(String accessToken, String name) {
        return getClaims(accessToken).get(name).asString();
    }
/*
    public boolean verifyToken(String accessToken, PrincipalDetails principalDetails) {
        boolean result = false;
        if (accessToken == null || accessToken.length() == 0) {
            return result;
        }
        return !isExpiredToken(accessToken) && (getClaim(accessToken, "username").equals(principalDetails.getUsername()));
    }*/

    public boolean verifyToken(String token) {
        boolean result = false;
        if (token == null || token.length() == 0) {
            return result;
        }
        return !isExpiredToken(token);
    }
}
