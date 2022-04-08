package com.project.mentoridge.modules.board.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
/*
    @Test
    void find_comments_by_postIds() {
        List<Comment> comments = commentRepository.findCommentsByPostIds(Arrays.asList(1L, 2L, 3L));
        System.out.println(comments);
    }*/

}