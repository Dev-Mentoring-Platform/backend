package com.project.mentoridge.modules.base;

import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.modules.account.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.TOKEN_PREFIX;

public abstract class AbstractControllerIntegrationTest {

    protected static final String NAME = "user";
    protected static final String USERNAME = "user@email.com";

    protected static final String MENTEE_NAME = "menteeUser";
    protected static final String MENTEE_USERNAME = "menteeUser@email.com";
    protected static final String MENTOR_NAME = "mentorUser";
    protected static final String MENTOR_USERNAME = "mentorUser@email.com";

    @Autowired
    JwtTokenManager jwtTokenManager;

    public String getAccessToken(String username, RoleType roleType) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", roleType);
        return TOKEN_PREFIX + jwtTokenManager.createToken(USERNAME, claims);
    }
}
