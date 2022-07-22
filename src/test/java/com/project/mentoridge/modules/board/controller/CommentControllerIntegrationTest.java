package com.project.mentoridge.modules.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.board.controller.request.CommentCreateRequest;
import com.project.mentoridge.modules.board.controller.request.CommentUpdateRequest;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.repository.CommentRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.service.CommentService;
import com.project.mentoridge.modules.board.service.PostService;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Post;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMenteeUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
public class CommentControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/posts/{post_id}/comments";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    LoginService loginService;

    private User user;
    private String accessToken;

    private User postWriter;
    private Post post;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        user = saveMenteeUser("user", loginService);
        accessToken = getAccessToken(user.getUsername(), RoleType.MENTEE);

        postWriter = saveMenteeUser("postWriter", loginService);
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build();
        post = postService.createPost(postWriter, postCreateRequest);
    }

    @Test
    void getComments() throws Exception {

        // Given
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("content")
                .build();
        Comment comment = commentService.createComment(user, post.getId(), commentCreateRequest);
        // When
        // Then
        mockMvc.perform(get(BASE_URL, post.getId())
                        .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.postId").exists())
                .andExpect(jsonPath("$.userNickname").exists())
                .andExpect(jsonPath("$.userImage").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void newComment() throws Exception {

        // Given
        // When
        // Then
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("content")
                .build();
        mockMvc.perform(post(BASE_URL, post.getId())
                        .header(AUTHORIZATION, accessToken)
                        .content(objectMapper.writeValueAsString(commentCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void newComment_with_no_content() throws Exception {

        // Given
        // When
        // Then
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("")
                .build();
        mockMvc.perform(post(BASE_URL, post.getId())
                        .header(AUTHORIZATION, accessToken)
                        .content(objectMapper.writeValueAsString(commentCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("Invalid Input"))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void editComment() throws Exception {

        // Given
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("content")
                .build();
        Comment comment = commentService.createComment(user, post.getId(), commentCreateRequest);

        // When
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequest.builder()
                .content("updated")
                .build();
        mockMvc.perform(put(BASE_URL + "/{comment_id}", post.getId(), comment.getId())
                        .header(AUTHORIZATION, accessToken)
                        .content(objectMapper.writeValueAsString(commentUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        Comment updatedComment = commentRepository.findById(comment.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertEquals(commentUpdateRequest.getContent(), updatedComment.getContent())
        );
    }

    @Test
    void editComment_not_exist_post() throws Exception {

        // Given
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("content")
                .build();
        Comment comment = commentService.createComment(user, post.getId(), commentCreateRequest);

        // When
        // Then
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequest.builder()
                .content("updated")
                .build();
        mockMvc.perform(put(BASE_URL + "/{comment_id}", 1000L, comment.getId())
                        .header(AUTHORIZATION, accessToken)
                        .content(objectMapper.writeValueAsString(commentUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    void deleteComment() throws Exception {

        // Given
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("content")
                .build();
        Comment comment = commentService.createComment(user, post.getId(), commentCreateRequest);

        // When
        mockMvc.perform(delete(BASE_URL + "/{comment_id}", post.getId(), comment.getId())
                        .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        assertThat(commentRepository.findById(comment.getId()).isPresent()).isFalse();
    }
}
