package com.project.mentoridge.config.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.mentoridge.config.security.PrincipalDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenManager {

    public JwtTokenManager(@Value("${jwt.secret}") String secret,
                           @Value("${jwt.token-validity-in-seconds}") long expiredAfter) {
        this.secret = secret;
        this.expiredAfter = expiredAfter * 1000;
    }

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    private final String secret;
    private final long expiredAfter;

    public String createToken(String subject, Map<String, Object> claims) {
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + expiredAfter))
                .withPayload(claims)
                .sign(Algorithm.HMAC256(secret));
    }

    public String createToken(Authentication authentication) {

        Object principal = authentication.getPrincipal();
        if (principal instanceof PrincipalDetails) {

            PrincipalDetails principalDetails = (PrincipalDetails) principal;
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", principalDetails.getUsername());
            return createToken(principalDetails.getUsername(), claims);
        }
        return null;
    }

    public Map<String, String> convertTokenToMap(String jwtToken) {
        Map<String, String> map = new HashMap<>();
        map.put("header", HEADER);
        map.put("token", TOKEN_PREFIX + jwtToken);

        return map;
    }

    public DecodedJWT getDecodedToken(String jwtToken) {
        if (jwtToken == null || jwtToken.length() == 0) {
            return null;
        }
        return JWT.require(Algorithm.HMAC256(secret)).build().verify(jwtToken);
    }

    private boolean isExpiredToken(String jwtToken) {
        return getDecodedToken(jwtToken).getExpiresAt().before(new Date());
    }

    private Map<String, Claim> getClaims(String jwtToken) {
        return getDecodedToken(jwtToken).getClaims();
    }

    public String getClaim(String jwtToken, String name) {
        return getClaims(jwtToken).get(name).asString();
    }

    public boolean verifyToken(String jwtToken, PrincipalDetails principalDetails) {
        boolean result = false;
        if (jwtToken == null || jwtToken.length() == 0) {
            return result;
        }
        return !isExpiredToken(jwtToken) && (getClaim(jwtToken, "username").equals(principalDetails.getUsername()));
    }
}
