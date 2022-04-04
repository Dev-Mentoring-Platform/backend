package com.project.mentoridge.modules.board.service;

import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.vo.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    PostService postService;
    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;

    // 글 등록
    @Test
    void create_post() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        Post post = Post.builder()
                .user(user)
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .build();
        when(postRepository.save(post)).thenReturn(post);

        // when
        PostCreateRequest createRequest = PostCreateRequest.builder()
                .title("title")
                .content("content")
                .category(CategoryType.LECTURE_REQUEST)
                .build();
        Post saved = postService.createPost(user, createRequest);

        // then
        assertAll(
                () -> assertThat(saved.getUser()).isEqualTo(user),
                () -> assertThat(saved.getCategory()).isEqualTo(createRequest.getCategory()),
                () -> assertThat(saved.getTitle()).isEqualTo(createRequest.getTitle()),
                () -> assertThat(saved.getContent()).isEqualTo(createRequest.getContent())
        );
    }

    // 글 수정
    @Test
    void update_post() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // 이미 등록된 상태
        Post post = Post.builder()
                .user(user)
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .build();
        when(postRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(post));

        // when
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .category(CategoryType.TALK)
                .title("title-update")
                .content("content-update")
                .build();
        postService.updatePost(user, 1L, updateRequest);

        // then
        assertAll(
                () -> assertThat(post.getUser()).isEqualTo(user),
                () -> assertThat(post.getCategory()).isEqualTo(updateRequest.getCategory()),
                () -> assertThat(post.getTitle()).isEqualTo(updateRequest.getTitle()),
                () -> assertThat(post.getContent()).isEqualTo(updateRequest.getContent())
        );
    }

    // 글 삭제
    @Test
    void delete_post() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // 이미 등록된 상태
        Post post = Post.builder()
                .user(user)
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .build();
        when(postRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(post));

        // when
        postService.deletePost(user, 1L);

        // then
        verify(postRepository).delete(post);
    }

    // 글 조회
    @Test
    void get_post() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // 이미 등록된 상태
        Post post = Post.builder()
                .user(user)
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .build();
        when(postRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(post));

        // when
        // then
    }
}