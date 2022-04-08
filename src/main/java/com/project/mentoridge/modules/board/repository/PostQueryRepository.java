package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.modules.board.controller.response.PostResponse;
import com.project.mentoridge.modules.board.repository.dto.PostCommentQueryDto;
import com.project.mentoridge.modules.board.repository.dto.PostLikingQueryDto;
import com.project.mentoridge.modules.board.vo.Post;
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
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class PostQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;

    /*
    SELECT post_id, COUNT(*) FROM comment
    WHERE post_id IN ()
    GROUP BY post_id
     */
    public Map<Long, Long> findPostCommentQueryDtoMap(List<Long> postIds) {
        List<PostCommentQueryDto> postCommentQueryDtos = em.createQuery("select new com.project.mentoridge.modules.board.repository.dto.PostCommentQueryDto(c.post.id, count(c.id)) " +
                        "from Comment c where c.post.id in :postIds group by c.post.id")
                .setParameter("postIds", postIds).getResultList();
        return postCommentQueryDtos.stream()
                .collect(Collectors.toMap(PostCommentQueryDto::getPostId, PostCommentQueryDto::getCommentCount));
    }

    public Map<Long, Long> findPostLikingQueryDtoMap(List<Long> postIds) {
        List<PostLikingQueryDto> postLikingQueryDtos = em.createQuery("select new com.project.mentoridge.modules.board.repository.dto.PostLikingQueryDto(l.post.id, count(l.id)) " +
                        "from Liking l where l.post.id in :postIds group by l.post.id")
                .setParameter("postIds", postIds).getResultList();
        return postLikingQueryDtos.stream()
                .collect(Collectors.toMap(PostLikingQueryDto::getPostId, PostLikingQueryDto::getLikingCount));
    }

    // 댓글단 글 리스트
    /*
     SELECT * FROM post
     WHERE post_id in (SELECT post_id FROM comment WHERE user_id = 2);
     */
    public Page<PostResponse> findCommentingPosts(Long userId, Pageable pageable) {

        List<Long> postIds = em.createQuery("select c.post.id from Comment c where c.user.id = :userId", Long.class)
                .setParameter("userId", userId).getResultList();
        return getPosts(postIds, pageable);
    }

    /*
     SELECT * FROM post
     WHERE post_id in (SELECT post_id FROM liking WHERE user_id = 2);
     */
    // 좋아요한 글 리스트
    public Page<PostResponse> findLikingPosts(Long userId, Pageable pageable) {

        List<Long> postIds = em.createQuery("select l.post.id from Liking l where l.user.id = :userId", Long.class)
                .setParameter("userId", userId).getResultList();
        return getPosts(postIds, pageable);
    }

    private PageImpl<PostResponse> getPosts(List<Long> postIds, Pageable pageable) {
        QueryResults<Post> posts = jpaQueryFactory.selectFrom(post)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(post.id.in(postIds))
                .fetchResults();
        List<PostResponse> result = posts.getResults().stream().map(PostResponse::new).collect(Collectors.toList());
        return new PageImpl<>(result, pageable, posts.getTotal());
    }
}
