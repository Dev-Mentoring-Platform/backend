package com.project.mentoridge.modules.board.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.controller.response.PostResponse;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.repository.ContentSearchRepository;
import com.project.mentoridge.modules.board.repository.LikingRepository;
import com.project.mentoridge.modules.board.repository.PostQueryRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.vo.Liking;
import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.modules.log.component.LikingLogService;
import com.project.mentoridge.modules.log.component.PostLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    PostService postService;
    @Mock
    PostRepository postRepository;
    @Mock
    PostQueryRepository postQueryRepository;
    @Mock
    ContentSearchRepository contentSearchRepository;
    @Mock
    PostLogService postLogService;
    @Mock
    UserRepository userRepository;
    @Mock
    LikingRepository likingRepository;
    @Mock
    LikingLogService likingLogService;

    @Test
    void get_post() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(user.getNickname()).thenReturn("user");
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        Post post = Post.builder()
                .user(user)
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // when
        PostResponse postResponse = postService.getPostResponse(user, 1L);

        // then
        assertAll(
                () -> assertThat(postResponse.getUserNickname()).isEqualTo("user"),
                () -> assertThat(postResponse.getCategory()).isEqualTo(post.getCategory()),
                () -> assertThat(postResponse.getTitle()).isEqualTo(post.getTitle()),
                () -> assertThat(postResponse.getContent()).isEqualTo(post.getContent()),
                () -> assertThat(postResponse.getHits()).isEqualTo(1)
        );
    }

    @Test
    void getPostResponsesOfUser() {

        // given
        User user = mock(User.class);
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        // when
        postService.getPostResponsesOfUser(user, 1);

        // then
        verify(postRepository).findByUser(eq(user), any(Pageable.class));
        // setCounts
        verify(postQueryRepository).findPostCommentQueryDtoMap(any(List.class));
        verify(postQueryRepository).findPostLikingQueryDtoMap(any(List.class));
    }

    @Test
    void getPostResponses_if_search_is_not_blank() {

        // given
        User user = mock(User.class);
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        // when
        postService.getPostResponses(user, "search", 1);

        // then
        verify(contentSearchRepository).findPostsSearchedByContent(eq("search"), any(Pageable.class));
        // setCounts
        verify(postQueryRepository).findPostCommentQueryDtoMap(any(List.class));
        verify(postQueryRepository).findPostLikingQueryDtoMap(any(List.class));
    }

    @Test
    void getPostResponses_if_search_is_blank() {

        // given
        User user = mock(User.class);
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        // when
        postService.getPostResponses(user, "", 1);

        // then
        verify(postRepository).findAll(any(Pageable.class));
        // setCounts
        verify(postQueryRepository).findPostCommentQueryDtoMap(any(List.class));
        verify(postQueryRepository).findPostLikingQueryDtoMap(any(List.class));
    }

    @Test
    void getPostResponse() {

        // given
        User user = mock(User.class);
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));
        Post post = mock(Post.class);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // when
        postService.getPostResponses(user, "", 1);

        // then
        verify(post).hit();
        verify(postRepository).findById(eq(1L));
        // setCount
        verify(postQueryRepository).findPostCommentQueryDtoMap(anyLong());
        verify(postQueryRepository).findPostLikingQueryDtoMap(anyLong());
    }

    @Test
    void getCommentingPostResponses() {

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(2L);
        // when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));
        // when
        postService.getCommentingPostResponses(user, 1);

        // then
        verify(postQueryRepository).findCommentingPosts(eq(2L), any(Pageable.class));
    }

    @Test
    void getLikingPostResponses() {

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(2L);
        // when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));
        // when
        postService.getLikingPostResponses(user, 1);

        // then
        verify(postQueryRepository).findLikingPosts(eq(2L), any(Pageable.class));
    }

    // 글 등록
    @Test
    void create_post() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        // when
        PostCreateRequest createRequest = PostCreateRequest.builder()
                .title("title")
                .content("content")
                .category(CategoryType.LECTURE_REQUEST)
                .build();
        Post saved = postService.createPost(user, createRequest);

        // then
        verify(postRepository).save(createRequest.toEntity(user));
        verify(postLogService).insert(eq(user), any(Post.class));
    }

    @Test
    void create_post_withoutAuthenticatedUser() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.empty());

        // when
        // then
        PostCreateRequest createRequest = PostCreateRequest.builder()
                .title("title")
                .content("content")
                .category(CategoryType.LECTURE_REQUEST)
                .build();
        assertThrows(UnauthorizedException.class,
                () -> postService.createPost(user, createRequest));
    }

    // 글 수정
    @Test
    void update_post() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        // 이미 등록된 상태
//        Post post = Post.builder()
//                .user(user)
//                .category(CategoryType.LECTURE_REQUEST)
//                .title("title")
//                .content("content")
//                .build();
        Post post = mock(Post.class);
        when(postRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(post));

        // when
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .category(CategoryType.TALK)
                .title("title-update")
                .content("content-update")
                .build();
        postService.updatePost(user, 1L, updateRequest);

        // then
        verify(post).update(eq(updateRequest), eq(user), eq(postLogService));
        // verify(postLogService).update(eq(user), any(Post.class), any(Post.class));
    }

    // 글 삭제
    @Test
    void delete_post() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        // 이미 등록된 상태
//        Post post = Post.builder()
//                .user(user)
//                .category(CategoryType.LECTURE_REQUEST)
//                .title("title")
//                .content("content")
//                .build();
        Post post = mock(Post.class);
        when(postRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(post));

        // when
        postService.deletePost(user, 1L);

        // then
        verify(post).delete(user, postLogService);
        verify(postRepository).delete(post);
        // verify(postLogService).delete(eq(user), any(Post.class));
    }

/*
    @Test
    void like_post() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        // 이미 등록된 상태
        Post post = Post.builder()
                .user(user)
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .build();
        when(postRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(post));

        // when
        postService.likePost(user, 1L);

        // then
        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();
        verify(likeRepository).save(like);
    }*/
/*
    @Test
    void cancel_like() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        // 이미 등록된 상태
        Post post = Post.builder()
                .user(user)
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .build();
        when(postRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(post));
        // 좋아요
        Like like = mock(Like.class);
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(like));

        // when
        postService.cancelPostLike(user, 1L);

        // then
        verify(likeRepository).delete(like);
    }*/

    @Test
    void like_post_when_not_liked() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

//        Post post = Post.builder()
//                .user(user)
//                .category(CategoryType.LECTURE_REQUEST)
//                .title("title")
//                .content("content")
//                .build();
        Post post = mock(Post.class);
        when(postRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(post));
        when(likingRepository.findByUserAndPost(user, post)).thenReturn(null);

        // when
        postService.likePost(user, 1L);

        // then
        verify(likingRepository).save(any(Liking.class));
        verify(likingLogService).insert(eq(user), any(Liking.class));
    }

    @Test
    void like_post_when_liked() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(userRepository.findByUsername("user@email.com")).thenReturn(Optional.of(user));

        // 이미 등록된 상태
//        Post post = Post.builder()
//                .user(user)
//                .category(CategoryType.LECTURE_REQUEST)
//                .title("title")
//                .content("content")
//                .build();
        Post post = mock(Post.class);
        when(postRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(post));
        Liking liking = mock(Liking.class);
        when(likingRepository.findByUserAndPost(user, post)).thenReturn(liking);

        // when
        postService.likePost(user, 1L);

        // then
        verify(liking).delete(eq(user), eq(likingLogService));
        verify(likingRepository).delete(liking);
        // verify(likingLogService).delete(eq(user), any(Liking.class));
    }
}