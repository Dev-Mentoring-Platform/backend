package com.project.mentoridge.modules.board.service;

import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.controller.request.CommentCreateRequest;
import com.project.mentoridge.modules.board.controller.request.CommentUpdateRequest;
import com.project.mentoridge.modules.board.repository.CommentRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.modules.log.component.CommentLogService;
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
    CommentLogService commentLogService;

    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;

    @Test
    void create_comment() {

        // given
        User postWriter = mock(User.class);
        User commentWriter = mock(User.class);
        when(commentWriter.getUsername()).thenReturn("commentWriter");
        when(userRepository.findByUsername("commentWriter")).thenReturn(Optional.of(commentWriter));

//        Post post = Post.builder()
//                .user(postWriter)
//                .category(CategoryType.LECTURE_REQUEST)
//                .title("title")
//                .content("content")
//                .build();
        Post post = mock(Post.class);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Comment comment = mock(Comment.class);
        CommentCreateRequest createRequest = mock(CommentCreateRequest.class);
        when(createRequest.toEntity(commentWriter, post)).thenReturn(comment);
        Comment saved = mock(Comment.class);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        // when
        commentService.createComment(commentWriter, 1L, createRequest);

        // then
        verify(commentRepository).save(any(Comment.class));
        verify(commentLogService).insert(commentWriter, saved);
    }

    @Test
    void update_comment() {

        // given
        User postWriter = mock(User.class);

        User commentWriter = mock(User.class);
        when(commentWriter.getUsername()).thenReturn("commentWriter");
        when(userRepository.findByUsername("commentWriter")).thenReturn(Optional.of(commentWriter));

        Post post = mock(Post.class);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Comment comment = mock(Comment.class);
        when(commentRepository.findByUserAndPostAndId(commentWriter, post, 1L)).thenReturn(Optional.of(comment));

        // when
        CommentUpdateRequest updateRequest = mock(CommentUpdateRequest.class);
        commentService.updateComment(commentWriter, 1L, 1L, updateRequest);

        // then
        verify(comment).update(eq(updateRequest), eq(commentWriter), eq(commentLogService));
        // verify(commentLogService).update(eq(commentWriter), any(Comment.class), any(Comment.class));
    }

    @Test
    void delete_comment() {

        // given
        User postWriter = mock(User.class);

        User commentWriter = mock(User.class);
        when(commentWriter.getUsername()).thenReturn("commentWriter");
        when(userRepository.findByUsername("commentWriter")).thenReturn(Optional.of(commentWriter));

        Post post = mock(Post.class);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Comment comment = mock(Comment.class);
        when(commentRepository.findByUserAndPostAndId(commentWriter, post, 1L)).thenReturn(Optional.of(comment));

        // when
        commentService.deleteComment(commentWriter, 1L, 1L);

        // then
        verify(comment).delete(commentWriter, commentLogService);
        verify(commentRepository).delete(comment);
        // verify(commentLogService).delete(eq(commentWriter), any(Comment.class));
    }
}