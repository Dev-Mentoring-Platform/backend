package com.project.mentoridge.config.security.oauth.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public abstract class OAuth {

    public static final String HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    protected final HttpSession session;
    protected final RestTemplate restTemplate;
    protected final ObjectMapper objectMapper;

    protected abstract String requestAccessToken(String code);

    protected abstract String requestUserInfo(String accessToken);

    protected String requestLogin(String code) {

        String accessToken = requestAccessToken(code);
        String userInfo = requestUserInfo(accessToken);

        return userInfo;
    }

    protected Map<String, String> convertStringToMap(String string) {

        try {
            if (StringUtils.hasLength(string)) {
                return objectMapper.readValue(string, Map.class);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Collections.EMPTY_MAP;
    }
}
