package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.configuration.annotation.RepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;

@RepositoryTest
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