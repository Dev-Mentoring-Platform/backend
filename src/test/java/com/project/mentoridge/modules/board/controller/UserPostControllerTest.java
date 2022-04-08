package com.project.mentoridge.modules.board.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.controller.response.PostResponse;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.service.PostService;
import com.project.mentoridge.modules.board.vo.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void get_post() throws Exception {

        // given
        Post post = Post.builder()
                .user(mock(User.class))
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .build();
        PostResponse postResponse = new PostResponse(post);
        when(postService.getPostResponse(any(User.class), anyLong())).thenReturn(postResponse);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{post_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").hasJsonPath())
                .andExpect(jsonPath("$.userNickname").hasJsonPath())
                .andExpect(jsonPath("$.category").hasJsonPath())
                .andExpect(jsonPath("$.title").hasJsonPath())
                .andExpect(jsonPath("$.content").hasJsonPath())
                .andExpect(jsonPath("$.createdAt").hasJsonPath());
    }

    @Test
    void update_post() throws Exception {

        // given
        doNothing().when(postService)
                .updatePost(any(User.class), anyLong(), any(PostUpdateRequest.class));

        // when
        // then
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("title")
                .content("content")
                .category(CategoryType.LECTURE_REQUEST)
                .build();
        mockMvc.perform(put(BASE_URL + "/{post_id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void delete_post() throws Exception {

        // given
        doNothing().when(postService).deletePost(any(User.class), anyLong());

        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{post_id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}