package com.project.mentoridge.config.security.oauth.provider.naver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.security.oauth.provider.OAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Component
// https://developers.naver.com/docs/login/api/api.md
public class NaverOAuth extends OAuth {

    private final String NAVER_BASE_URL = "https://nid.naver.com/oauth2.0/authorize";
    private final String NAVER_CALLBACK_URL = "http://localhost:8080/oauth/naver/callback";
    private final String NAVER_USERINFO_ACCESS_URL = "https://openapi.naver.com/v1/nid/me";
    private final String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";

    private final String NAVER_CLIENT_ID;
    private final String NAVER_CLIENT_SECRET;

    @Autowired
    public NaverOAuth(HttpSession session, RestTemplate restTemplate, ObjectMapper objectMapper) {
//                      @Value("${security.oauth2.naver.client-id}") String clientId,
//                      @Value("${security.oauth2.naver.client-secret}") String clientSecret) {
        super(session, restTemplate, objectMapper);
        this.NAVER_CLIENT_ID = "clientId";
        this.NAVER_CLIENT_SECRET = "clientSecret";
    }

    @Override
    public String requestAccessToken(String code) {

        if (!StringUtils.hasLength(code)) {
            return null;
        }

        /*
        * Required Parameter
        - grant_type
        - client_id
        - client_secret
        - code
        - state
        */
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        try {
            params.add("client_id", URLEncoder.encode(NAVER_CLIENT_ID, "UTF-8"));
            params.add("client_secret", URLEncoder.encode(NAVER_CLIENT_SECRET, "UTF-8"));
            params.add("grant_type", URLEncoder.encode("authorization_code", "UTF-8"));
            params.add("state", URLEncoder.encode((String) session.getAttribute("state"), "UTF-8"));
            params.add("code", URLEncoder.encode(code, "UTF-8"));
            params.add("redirect_uri", URLEncoder.encode(NAVER_CALLBACK_URL, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(NAVER_TOKEN_URL, HttpMethod.POST, request, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // System.out.println(responseEntity.getBody());
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
        String token = map.get("access_token");
        if (!StringUtils.hasLength(token)) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER, TOKEN_PREFIX + token);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(NAVER_USERINFO_ACCESS_URL, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }

        return null;

    }

    public NaverResponse getUserInfo(String code) {

        try {
            NaverResponse naverResponse = objectMapper.readValue(requestLogin(code), NaverResponse.class);
            return naverResponse;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
