package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.modules.board.controller.request.ConentSearchRequest;
import com.project.mentoridge.modules.board.controller.response.PostResponse;
import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.modules.board.vo.QComment;
import com.project.mentoridge.modules.board.vo.QPost;
import com.project.mentoridge.modules.lecture.repository.dto.LectureMentorQueryDto;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class ContentSearchRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;
    private final QComment comment = QComment.comment;

    // SELECT * FROM post p INNER JOIN comment c ON p.post_id = c.post_id WHERE p.title LIKE '%content%' OR p.CONTENT LIKE '%content%' OR c.CONTENT LIKE '%content%'
    public Page<PostResponse> findPostsSearchedByContent(ConentSearchRequest searchRequest, Pageable pageable) {

        if (searchRequest != null && StringUtils.isNotBlank(searchRequest.getContent())) {
            String content = searchRequest.getContent();

            // 각각 조회
            // post
            QueryResults<Post> posts = jpaQueryFactory.selectFrom(post)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .where(post.title.containsIgnoreCase(content), post.content.containsIgnoreCase(content))
                    .fetchResults();

            // comment

        } else {



        }
        return null;
    }
}
