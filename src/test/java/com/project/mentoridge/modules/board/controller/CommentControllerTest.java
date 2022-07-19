package com.project.mentoridge.modules.board.controller;

import com.project.mentoridge.modules.account.controller.CareerController;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.board.controller.request.CommentCreateRequest;
import com.project.mentoridge.modules.board.controller.request.CommentUpdateRequest;
import com.project.mentoridge.modules.board.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class CommentControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/posts/{post_id}/comments";

    @MockBean
    CommentService commentService;

    @Test
    void get_comments() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL, 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(commentService).getCommentResponses(any(User.class), eq(1L), eq(1));
    }

    @Test
    void new_comment() throws Exception {

        // given
        // when
        // then
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("content")
                .build();
        mockMvc.perform(post(BASE_URL, 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(commentService).createComment(any(User.class), eq(1L), eq(commentCreateRequest));
    }

    @Test
    void new_comment_with_invalid_input() throws Exception {

        // given
        // when
        // then
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("")
                .build();
        mockMvc.perform(post(BASE_URL, 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_comment() throws Exception {

        // given
        // when
        // then
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequest.builder()
                .content("content-update")
                .build();
        mockMvc.perform(put(BASE_URL + "/{comment_id}", 1L, 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(commentService).updateComment(any(User.class), eq(1L), eq(1L), eq(commentUpdateRequest));
    }

    @Test
    void delete_comment() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{comment_id}", 1L, 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(commentService).deleteComment(any(User.class), eq(1L), eq(1L));
    }
}