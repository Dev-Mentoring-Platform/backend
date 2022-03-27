package com.project.mentoridge.config.security.oauth.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomOAuth2UserServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;
}