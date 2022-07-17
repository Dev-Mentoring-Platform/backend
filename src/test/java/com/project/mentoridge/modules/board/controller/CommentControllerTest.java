package com.project.mentoridge.modules.board.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.interceptor.AuthInterceptor;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.PrincipalDetailsService;
import com.project.mentoridge.config.security.jwt.JwtRequestFilter;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.board.controller.request.CommentCreateRequest;
import com.project.mentoridge.modules.board.controller.request.CommentUpdateRequest;
import com.project.mentoridge.modules.board.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/posts/{post_id}/comments";

    @InjectMocks
    CommentController commentController;
    @Mock
    CommentService commentService;

    @BeforeEach
    @Override
    protected void init() {
        super.init();
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .addFilter(jwtRequestFilter)
                .addInterceptors(authInterceptor)
                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
    }

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