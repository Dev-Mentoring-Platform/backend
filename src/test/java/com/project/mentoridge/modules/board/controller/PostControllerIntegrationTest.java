package com.project.mentoridge.modules.board.controller;

import com.project.mentoridge.configuration.annotation.MockMvcTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@MockMvcTest
public class PostControllerIntegrationTest {

    private final static String BASE_URL = "/api/posts";

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    
}
