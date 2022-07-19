package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ServiceTest
class CommentLogServiceTest {

    @Autowired
    CommentLogService commentLogService;

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User postWriter = mock(User.class);
        when(postWriter.getNickname()).thenReturn("postWriter");
        Post post = Post.builder()
                .user(postWriter)
                .title("title")
                .category(CategoryType.LECTURE_REQUEST)
                .content("post_content")
                .build();

        User commentWriter = mock(User.class);
        when(commentWriter.getNickname()).thenReturn("commentWriter");
        when(commentWriter.getUsername()).thenReturn("commentWriter");
        Comment comment = Comment.builder()
                .post(post)
                .user(commentWriter)
                .content("content")
                .build();

        // when
        String log = commentLogService.insert(commentWriter, comment);
        // then
        assertEquals(String.format("[Comment] 글 : %s, 댓글 작성자 : %s, 내용 : %s",
                comment.getPost().getTitle(), comment.getUser().getNickname(), comment.getContent()), log);

    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User postWriter = mock(User.class);
        when(postWriter.getNickname()).thenReturn("postWriter");
        Post post = Post.builder()
                .user(postWriter)
                .title("title")
                .category(CategoryType.LECTURE_REQUEST)
                .content("post_content")
                .build();

        User commentWriter = mock(User.class);
        when(commentWriter.getNickname()).thenReturn("commentWriter");
        when(commentWriter.getUsername()).thenReturn("commentWriter");
        Comment before = Comment.builder()
                .post(post)
                .user(commentWriter)
                .content("content_before")
                .build();
        Comment after = Comment.builder()
                .post(post)
                .user(commentWriter)
                .content("content_after")
                .build();

        // when
        String log = commentLogService.update(commentWriter, before, after);
        // then
        assertEquals(String.format("[Comment] 내용 : %s → %s", before.getContent(), after.getContent()), log);
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User postWriter = mock(User.class);
        when(postWriter.getNickname()).thenReturn("postWriter");
        Post post = Post.builder()
                .user(postWriter)
                .title("title")
                .category(CategoryType.LECTURE_REQUEST)
                .content("post_content")
                .build();

        User commentWriter = mock(User.class);
        when(commentWriter.getNickname()).thenReturn("commentWriter");
        when(commentWriter.getUsername()).thenReturn("commentWriter");
        Comment comment = Comment.builder()
                .post(post)
                .user(commentWriter)
                .content("content")
                .build();

        // when
        String log = commentLogService.delete(commentWriter, comment);
        // then
        assertEquals(String.format("[Comment] 글 : %s, 댓글 작성자 : %s, 내용 : %s",
                comment.getPost().getTitle(), comment.getUser().getNickname(), comment.getContent()), log);
    }
}