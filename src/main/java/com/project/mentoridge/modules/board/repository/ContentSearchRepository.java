package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.modules.board.controller.request.ContentSearchRequest;
import com.project.mentoridge.modules.board.controller.response.PostResponse;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.modules.board.vo.QComment;
import com.project.mentoridge.modules.board.vo.QPost;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class ContentSearchRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;
    private final QComment comment = QComment.comment;

    // SELECT * FROM post p INNER JOIN comment c ON p.post_id = c.post_id WHERE p.title LIKE '%content%' OR p.CONTENT LIKE '%content%' OR c.CONTENT LIKE '%content%'
    public Page<PostResponse> findPostsSearchedByContent(String content, Pageable pageable) {

        // assertNotNull(searchRequest);
        // String content = searchRequest.getContent();

        // 각각 조회
        // comment
        List<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .where(comment.content.containsIgnoreCase(content)).fetchResults().getResults();
        List<Long> postIds = comments.stream()
                .map(comment -> comment.getPost().getId()).collect(Collectors.toList());

        // post
        QueryResults<Post> posts = jpaQueryFactory.selectFrom(post)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(post.title.containsIgnoreCase(content).or(post.content.containsIgnoreCase(content)).or(post.id.in(postIds)))
                .fetchResults();

        List<PostResponse> postResponses = posts.getResults().stream()
                .map(PostResponse::new).collect(Collectors.toList());
        return new PageImpl<>(postResponses, pageable, posts.getTotal());
    }
}
