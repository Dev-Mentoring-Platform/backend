package com.project.mentoridge.modules.board.service;

import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.controller.request.CommentCreateRequest;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.repository.CommentRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;

    @Test
    void create_comment() {

        // given
        User postWriter = mock(User.class);
        when(postWriter.getUsername()).thenReturn("postWriter");
        when(userRepository.findByUsername("postWriter")).thenReturn(Optional.of(postWriter));

        User commentWriter = mock(User.class);
        when(commentWriter.getUsername()).thenReturn("commentWriter");
        when(userRepository.findByUsername("commentWriter")).thenReturn(Optional.of(commentWriter));

        Post post = Post.builder()
                .user(postWriter)
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .build();
        when(postRepository.findByUserAndId(postWriter, 1L)).thenReturn(Optional.of(post));

        // when
        CommentCreateRequest createRequest = CommentCreateRequest.builder()
                .content("content")
                .build();
        commentService.createComment(commentWriter, 1L, createRequest);

        // then
        Comment comment = createRequest.toEntity(commentWriter, post);
        verify(commentRepository).save(comment);
    }

    @Test
    void update_comment() {

        // given
        User postWriter = mock(User.class);
        when(postWriter.getUsername()).thenReturn("postWriter");
        when(userRepository.findByUsername("postWriter")).thenReturn(Optional.of(postWriter));

        User commentWriter = mock(User.class);
        when(commentWriter.getUsername()).thenReturn("commentWriter");
        when(userRepository.findByUsername("commentWriter")).thenReturn(Optional.of(commentWriter));

        Post post = Post.builder()
                .user(postWriter)
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .build();
        when(postRepository.findByUserAndId(postWriter, 1L)).thenReturn(Optional.of(post));

        // when
        // then
    }

    @Test
    void delete_comment() {

        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // when
        // then
    }
}