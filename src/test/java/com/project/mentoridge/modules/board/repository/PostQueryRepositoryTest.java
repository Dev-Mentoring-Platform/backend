package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.modules.board.controller.response.PostResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Map;

@SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
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

    @Test
    void findPostCommentQueryDtoMap() {
        Map<Long, Long> postCommentQueryDtoMap = postQueryRepository.findPostCommentQueryDtoMap(Arrays.asList(1L, 2L, 3L));
        System.out.println(postCommentQueryDtoMap);
    }

    @Test
    void findPostLikingQueryDtoMap() {
        Map<Long, Long> postLikingQueryDtoMap = postQueryRepository.findPostLikingQueryDtoMap(Arrays.asList(1L, 2L, 3L));
        System.out.println(postLikingQueryDtoMap);
    }
}