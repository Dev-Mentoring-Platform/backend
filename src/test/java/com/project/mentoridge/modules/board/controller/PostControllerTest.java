package com.project.mentoridge.modules.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.service.PostService;
import org.apache.tomcat.jni.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private final static String BASE_URL = "/api/posts";

    @InjectMocks
    PostController postController;
    @Mock
    PostService postService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    @Test
    void newPost() throws Exception {

        // given
        // when
        // then

    }

    @Test
    void newPost_withoutUser() throws Exception {

        // given
        // when
        // then
    }

}