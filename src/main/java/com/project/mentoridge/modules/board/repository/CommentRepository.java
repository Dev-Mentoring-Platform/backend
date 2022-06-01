package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {

//    @Query(value = "select c from Comment c where c.post.id in :postIds")
//    List<Comment> findCommentsByPostIds(@Param("postIds") List<Long> postIds);

    Page<Comment> findByPost(Post post, Pageable pageable);

    @Transactional
    @Modifying
    void deleteByUser(User user);
}
