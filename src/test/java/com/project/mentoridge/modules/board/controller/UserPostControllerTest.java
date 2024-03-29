package com.project.mentoridge.modules.board.controller;

import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserPostController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class UserPostControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/users/my-posts";

    @MockBean
    PostService postService;


    @Test
    void get_paged_my_posts() throws Exception {

        // given
        // when
        mockMvc.perform(get(BASE_URL)
                        .param("page", "2")
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(postService).getPostResponsesOfUser(user, 2);
    }

    @Test
    void get_post() throws Exception {

        // given
/*      Post post = Post.builder()
                .user(mock(User.class))
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .build();
        PostResponse postResponse = new PostResponse(post);*/
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{post_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(postService).getPostResponse(user, 1L);
    }

    @Test
    void update_post() throws Exception {

        // given
        // when
        // then
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("title")
                .content("content")
                .category(CategoryType.LECTURE_REQUEST)
                .build();
        mockMvc.perform(put(BASE_URL + "/{post_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(postService).updatePost(eq(user), eq(1L), any(PostUpdateRequest.class));
    }

    @Test
    void update_post_with_invalid_input() throws Exception {

        // given
        // when
        // then
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("title")
                .content("")
                .category(CategoryType.LECTURE_REQUEST)
                .build();
        mockMvc.perform(put(BASE_URL + "/{post_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_post() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{post_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(postService).deletePost(user, 1L);
    }

    @Test
    void get_paged_commenting_posts() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/commenting")
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(postService).getCommentingPostResponses(user, 1);
    }

    @Test
    void get_paged_liking_posts() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/liking")
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .param("page", "2"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(postService).getLikingPostResponses(user, 2);
    }
}