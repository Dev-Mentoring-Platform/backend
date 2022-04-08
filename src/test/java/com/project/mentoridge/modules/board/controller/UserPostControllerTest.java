package com.project.mentoridge.modules.board.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.board.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserPostControllerTest {

    private final static String BASE_URL = "/api/users/my-posts";

    @InjectMocks
    UserPostController userPostController;
    @Mock
    PostService postService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {

        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        mockMvc = MockMvcBuilders.standaloneSetup(userPostController)
                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
    }

    @Test
    void get_posts() throws Exception {

        // given
        // when
        // then
    }

    @Test
    void get_post() throws Exception {

        // given
        // when
        // then
    }

    @Test
    void update_post() throws Exception {

        // given
        // when
        // then
    }

    @Test
    void delete_post() throws Exception {

        // given
        // when
        // then
    }
}