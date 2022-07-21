package com.project.mentoridge.modules.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.board.controller.request.CommentCreateRequest;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.repository.CommentRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.service.CommentService;
import com.project.mentoridge.modules.board.service.PostService;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Post;
import org.junit.jupiter.api.BeforeAll;
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
public class UserPostControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/users/my-posts";

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

    @BeforeAll
    @Override
    protected void init() {
        super.init();
        user = saveMenteeUser("user", loginService);
        accessToken = getAccessToken(user.getUsername(), RoleType.MENTEE);
    }

    @Test
    void get_posts_of_user() throws Exception {

        // given
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build();
        Post post = postService.createPost(user, postCreateRequest);

        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").exists())
                .andExpect(jsonPath("$.userNickname").exists())
                .andExpect(jsonPath("$.userImage").exists())
                .andExpect(jsonPath("$.category").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.hits").exists())
                .andExpect(jsonPath("$.likingCount").exists())
                .andExpect(jsonPath("$.commentCount").exists());
    }

    @Test
    void get_post() throws Exception {

        // given
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build();
        Post post = postService.createPost(user, postCreateRequest);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{post_id}", post.getId())
                        .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").exists())
                .andExpect(jsonPath("$.userNickname").exists())
                .andExpect(jsonPath("$.userImage").exists())
                .andExpect(jsonPath("$.category").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.hits").exists())
                .andExpect(jsonPath("$.likingCount").exists())
                .andExpect(jsonPath("$.commentCount").exists());
    }

    @Test
    void edit_post() throws Exception {

        // given
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build();
        Post post = postService.createPost(user, postCreateRequest);

        // when
        // then
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .category(CategoryType.LECTURE_REQUEST)
                .title("updated_title")
                .content("updated_content")
                .image("updated_image")
                .build();
        mockMvc.perform(put(BASE_URL + "/{post_id}", post.getId())
                        .header(AUTHORIZATION, accessToken)
                        .content(objectMapper.writeValueAsString(postUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        Post updatedPost = postRepository.findById(post.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertEquals(postUpdateRequest.getCategory(), updatedPost.getCategory()),
                () -> assertEquals(postUpdateRequest.getTitle(), updatedPost.getTitle()),
                () -> assertEquals(postUpdateRequest.getContent(), updatedPost.getContent()),
                () -> assertEquals(postUpdateRequest.getImage(), updatedPost.getImage())
        );
    }

    @Test
    void delete_post() throws Exception {

        // given
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build();
        Post post = postService.createPost(user, postCreateRequest);

        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{post_id}", post.getId())
                        .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(postRepository.findById(post.getId()).isPresent()).isFalse();
    }


    @Test
    void get_commenting_posts() throws Exception {

        // given
        User postWriter = saveMenteeUser("postWriter", loginService);
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build();
        Post post = postService.createPost(postWriter, postCreateRequest);

        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("content")
                .build();
        Comment comment = commentService.createComment(user, post.getId(), commentCreateRequest);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/commenting")
                        .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").exists())
                .andExpect(jsonPath("$.userNickname").exists())
                .andExpect(jsonPath("$.userImage").exists())
                .andExpect(jsonPath("$.category").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.hits").exists())
                .andExpect(jsonPath("$.likingCount").exists())
                .andExpect(jsonPath("$.commentCount").exists());
    }

    @Test
    void get_liking_posts() throws Exception {

        // given
        User postWriter = saveMenteeUser("postWriter", loginService);
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build();
        Post post = postService.createPost(postWriter, postCreateRequest);

        postService.likePost(user, post.getId());

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/liking")
                        .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").exists())
                .andExpect(jsonPath("$.userNickname").exists())
                .andExpect(jsonPath("$.userImage").exists())
                .andExpect(jsonPath("$.category").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.hits").exists())
                .andExpect(jsonPath("$.likingCount").exists())
                .andExpect(jsonPath("$.commentCount").exists());
    }
}
