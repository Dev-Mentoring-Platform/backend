package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.vo.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class PostLogServiceTest {

    @Autowired
    PostLogService postLogService;

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User postWriter = mock(User.class);
        when(postWriter.getNickname()).thenReturn("postWriter");
        Post post = Post.builder()
                .user(postWriter)
                .title("title")
                .category(CategoryType.LECTURE_REQUEST)
                .content("content")
                .build();
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        postLogService.insert(pw, post);

        // then
        assertEquals(String.format("[Post] 글 작성자 : %s, 카테고리 : %s, 제목 : %s, 내용 : %s",
                post.getUser().getNickname(), post.getCategory(), post.getTitle(), post.getContent()), sw.toString());
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User postWriter = mock(User.class);
        when(postWriter.getNickname()).thenReturn("postWriter");
        Post before = Post.builder()
                .user(postWriter)
                .title("title")
                .category(CategoryType.LECTURE_REQUEST)
                .content("content")
                .build();
        Post after = Post.builder()
                .user(postWriter)
                .title("title_update")
                .category(CategoryType.TALK)
                .content("content_update")
                .build();

        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        postLogService.update(pw, before, after);

        // then
        assertEquals(String.format("[Post] 카테고리 : %s → %s, 제목 : %s → %s, 내용 : %s → %s",
                before.getCategory(), after.getCategory(),
                before.getTitle(), after.getTitle(),
                before.getContent(), after.getContent()), sw.toString());
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
                .content("content")
                .build();
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        postLogService.delete(pw, post);

        // then
        assertEquals(String.format("[Post] 글 작성자 : %s, 카테고리 : %s, 제목 : %s, 내용 : %s",
                post.getUser().getNickname(), post.getCategory(), post.getTitle(), post.getContent()), sw.toString());
    }
}