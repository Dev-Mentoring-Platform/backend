package com.project.mentoridge.modules.board.controller;

import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/posts";

    @InjectMocks
    PostController postController;
    @Mock
    PostService postService;

    @BeforeEach
    @Override
    protected void init() {
        super.init();
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .addFilter(jwtRequestFilter)
                .addInterceptors(authInterceptor)
                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
    }

    @Test
    void get_categories() throws Exception {

        // given
        // when
        // then
        String response = mockMvc.perform(get(BASE_URL + "/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(response).contains(Arrays.asList(CategoryType.LECTURE_REQUEST.name(), CategoryType.TALK.name()));
    }

    @Test
    void get_posts() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .param("search", "search")
                        .param("page", "2")
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(postService).getPostResponses(eq(user), eq("search"), eq(2));
    }

    @Test
    void get_post() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{post_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(postService).getPostResponse(eq(user), 1L);
    }

    @Test
    void new_post() throws Exception {

        // given
        // when
        // then
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("title")
                .content("content")
                .category(CategoryType.LECTURE_REQUEST)
                .build();
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(postService).createPost(eq(user), eq(postCreateRequest));
    }

    @Test
    void new_post_with_invalid_input() throws Exception {

        // given
        // when
        // then
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("")
                .content("content")
                .category(CategoryType.LECTURE_REQUEST)
                .build();
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void newPost_withoutUser() throws Exception {

        // given
        // when
        // then
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("title")
                .content("content")
                .category(CategoryType.LECTURE_REQUEST)
                .build();
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(postService);
    }

    @Test
    void likePost() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(post(BASE_URL + "/{post_id}/like", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(postService).likePost(eq(user), eq(1L));
    }
}