package com.project.mentoridge.modules.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.repository.LikingRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.service.PostService;
import com.project.mentoridge.modules.board.vo.Liking;
import com.project.mentoridge.modules.board.vo.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
public class PostControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/posts";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    LikingRepository likingRepository;
    @Autowired
    LoginService loginService;

    private User user;
    private String accessToken;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        user = saveMenteeUser("user", loginService);
        accessToken = getAccessToken(user.getUsername(), RoleType.MENTEE);
/*
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build();
        post = postService.createPost(user, postCreateRequest);*/
    }

    @Test
    void getCategories() throws Exception {

        // given
        // when
        // then
        String response = mockMvc.perform(get(BASE_URL + "/categories")
                        .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(response).contains(CategoryType.TALK.name(), CategoryType.LECTURE_REQUEST.name());
    }

    @Test
    void getPosts() throws Exception {

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
                        .header(AUTHORIZATION, accessToken)
                        // search
                        .param("search", "title"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].postId").value(post.getId()))
                .andExpect(jsonPath("$.content[0].userNickname").value(user.getNickname()))
                .andExpect(jsonPath("$.content[0].userImage").value(user.getImage()))
                .andExpect(jsonPath("$.content[0].category").value(post.getCategory().name()))
                .andExpect(jsonPath("$.content[0].title").value(post.getTitle()))
                .andExpect(jsonPath("$.content[0].content").value(post.getContent()))
                .andExpect(jsonPath("$.content[0].createdAt").exists())

                .andExpect(jsonPath("$.content[0].hits").value(0))
                .andExpect(jsonPath("$.content[0].likingCount").value(0L))
                .andExpect(jsonPath("$.content[0].commentCount").value(0L));
    }

    @Test
    void getPost() throws Exception {

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

        /*
          {
            "postId": 11,
            "userNickname": "userNickname",
            "userImage": null,
            "category": "TALK",
            "title": "title",
            "content": "content",
            "createdAt": "2022-07-23T16:31:32.1825262",
            "hits": 1,
            "likingCount": 0,
            "commentCount": 0
          }
        */
        mockMvc.perform(get(BASE_URL + "/{post_id}", post.getId())
                        .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.userNickname").value(user.getNickname()))
                .andExpect(jsonPath("$.userImage").value(user.getImage()))
                .andExpect(jsonPath("$.category").value(post.getCategory().name()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.hits").value(1))

                .andExpect(jsonPath("$.likingCount").value(0L))
                .andExpect(jsonPath("$.commentCount").value(0L));
    }

    @Test
    void newPost() throws Exception {

        // given
        // when
        // then
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build();
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessToken)
                        .content(objectMapper.writeValueAsString(postCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void newPost_with_invalid_input() throws Exception {

        // given
        // when
        // then
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("")
                .content("")
                .image("image")
                .build();
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessToken)
                        .content(objectMapper.writeValueAsString(postCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$..field", notNullValue()));
    }

    @Test
    void likePost_without_Auth() throws Exception {

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
        mockMvc.perform(post(BASE_URL + "/{post_id}/like", post.getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void likePost() throws Exception {

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
        mockMvc.perform(post(BASE_URL + "/{post_id}/like", post.getId())
                        .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk());
        Liking liking = likingRepository.findByUserAndPost(user, post);
        assertNotNull(liking);
    }

    @Test
    void cancel_likePost() throws Exception {

        // given
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build();
        Post post = postService.createPost(user, postCreateRequest);
        postService.likePost(user, post.getId());

        // when
        // then
        mockMvc.perform(post(BASE_URL + "/{post_id}/like", post.getId())
                        .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk());
        Liking liking = likingRepository.findByUserAndPost(user, post);
        assertNull(liking);
    }
}
