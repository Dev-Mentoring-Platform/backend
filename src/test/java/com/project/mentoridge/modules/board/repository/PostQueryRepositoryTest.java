package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.modules.board.controller.response.PostResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class PostQueryRepositoryTest {

    @Autowired
    PostQueryRepository postQueryRepository;

    @Test
    void findCommentingPosts() {
        Page<PostResponse> posts = postQueryRepository.findCommentingPosts(1L, Pageable.ofSize(10));
        System.out.println(posts);
    }

    @Test
    void findLikingPosts() {
        Page<PostResponse> posts = postQueryRepository.findLikingPosts(1L, Pageable.ofSize(10));
        System.out.println(posts);
    }
}