package com.project.mentoridge.modules.base;

import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.modules.account.enums.RoleType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
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
    protected EntityManager em;

    @Autowired
    JwtTokenManager jwtTokenManager;

    public String getAccessToken(String username, RoleType roleType) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", roleType.getType());
        return TOKEN_PREFIX + jwtTokenManager.createToken(username, claims);
    }
    // java.lang.IllegalStateException: Not allowed to create transaction on shared EntityManager - use Spring transactions or EJB CMT instead
    protected void initDatabase() {
        String[] tables = {"notice", "inquiry", "liking", "comment", "post", "notification", "pick", "mentor_review", "mentee_review", "enrollment", "message", "chatroom", "lecture_price", "lecture_subject", "lecture_system_type", "lecture", "career", "education", "mentor", "mentee", "user", "subject", "address"};
        for(String table: tables) {
            em.createNativeQuery("delete from " + table).executeUpdate();
        }
    }

    protected void init() {
        initDatabase();
    }
}
