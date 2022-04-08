package com.project.mentoridge.modules.board.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.controller.request.CommentCreateRequest;
import com.project.mentoridge.modules.board.controller.request.CommentUpdateRequest;
import com.project.mentoridge.modules.board.controller.response.CommentResponse;
import com.project.mentoridge.modules.board.service.CommentService;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private final static String BASE_URL = "/api/posts/{post_id}/comments";

    @InjectMocks
    CommentController commentController;
    @Mock
    CommentService commentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {

        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
    }

    @Test
    void get_comments() throws Exception {

        // given
        Post post = mock(Post.class);
        when(post.getId()).thenReturn(1L);

        User user1 = mock(User.class);
        when(user1.getNickname()).thenReturn("user1");
        User user2 = mock(User.class);
        when(user2.getNickname()).thenReturn("user2");
        Comment comment1 = Comment.builder()
                .user(user1)
                .post(post)
                .content("content1")
                .build();
        Comment comment2 = Comment.builder()
                .user(user2)
                .post(post)
                .content("content2")
                .build();
        Page<CommentResponse> response = new PageImpl<>(Arrays.asList(new CommentResponse(comment1), new CommentResponse(comment2)), Pageable.ofSize(20), 2);
        doReturn(response)
                .when(commentService).getCommentResponses(any(User.class), anyLong(), anyInt());

        // when
        // then
        mockMvc.perform(get(BASE_URL, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..postId").hasJsonPath())
                .andExpect(jsonPath("$..userNickname").hasJsonPath())
                .andExpect(jsonPath("$..content").hasJsonPath())
                .andExpect(jsonPath("$..createdAt").hasJsonPath());
    }

    @Test
    void new_comment() throws Exception {

        // given
        Comment comment = Comment.builder()
                .user(mock(User.class))
                .post(mock(Post.class))
                .content("content")
                .build();
        when(commentService.createComment(any(User.class), anyLong(), any(CommentCreateRequest.class)))
                .thenReturn(comment);
        // when
        // then
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("content")
                .build();
        mockMvc.perform(post(BASE_URL, 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void update_comment() throws Exception {

        // given
        doNothing().when(commentService)
                .updateComment(any(User.class), anyLong(), anyLong(), any(CommentUpdateRequest.class));

        // when
        // then
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequest.builder()
                .content("content-update")
                .build();
        mockMvc.perform(put(BASE_URL + "/{comment_id}", 1L, 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void delete_comment() throws Exception {

        // given
        doNothing().when(commentService).deleteComment(any(User.class), anyLong(), anyLong());

        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{comment_id}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}