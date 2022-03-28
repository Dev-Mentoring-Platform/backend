package com.project.mentoridge.config.security.oauth.provider.google;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.security.oauth.provider.AuthorizeResult;
import com.project.mentoridge.config.security.oauth.provider.OAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
// https://developers.google.com/identity/protocols/oauth2/web-server#httprest_1
public class GoogleOAuth extends OAuth {

    private final String GOOGLE_BASE_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private final String GOOGLE_CALLBACK_URL = "https://a68323ea2219.ngrok.io/oauth/google/callback";
    private final String GOOGLE_USERINFO_ACCESS_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    private final String GOOLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    private final String GOOGLE_CLIENT_ID;
    private final String GOOGLE_CLIENT_SECRET;

    @Autowired
    public GoogleOAuth(RestTemplate restTemplate, ObjectMapper objectMapper) {
//                       @Value("${security.oauth2.google.client-id}") String clientId,
//                       @Value("${security.oauth2.google.client-secret}") String clientSecret) {
        super(null, restTemplate, objectMapper);
        this.GOOGLE_CLIENT_ID = "clientId";
        this.GOOGLE_CLIENT_SECRET = "clientSecret";
    }

    // Exchange authorization code for refresh and access tokens
    @Override
    public String requestAccessToken(String code) {

        if (!StringUtils.hasLength(code)) {
            return null;
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", GOOGLE_CLIENT_ID);
        params.add("client_secret", GOOGLE_CLIENT_SECRET);
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", GOOGLE_CALLBACK_URL);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOLE_TOKEN_URL, params, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }

        return null;
    }

    @Override
    public String requestUserInfo(String accessToken) {

        if (!StringUtils.hasLength(accessToken)) {
            return null;
        }

        Map<String, String> map = convertStringToMap(accessToken);
        // System.out.println(map);
        String token = map.get("access_token");
        if (!StringUtils.hasLength(token)) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER, TOKEN_PREFIX + token);
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(GOOGLE_USERINFO_ACCESS_URL, HttpMethod.GET, new HttpEntity(headers), String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }
        return null;
    }

    public Map<String, String> getUserInfo(String code) {
        return convertStringToMap(requestLogin(code));
    }

    public Map<String, String> getUserInfo(AuthorizeResult.UserInfo user) {

        String userInfo = null;
        try {
            userInfo = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return convertStringToMap(userInfo);
    }
}
