package com.project.mentoridge.modules.board.repository;


import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.vo.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUserAndId(User user, Long postId);
    List<Post> findByUser(User user);
    Page<Post> findByUser(User user, Pageable pageable);

    @Query(value = "select p.id from Post p where p.user = :user")
    List<Long> findIdsByUser(@Param("user") User user);

    @Transactional
    @Modifying
    void deleteByUser(User user);

    @Transactional
    @Modifying
    @Query(value = "delete from Post p where p.id in :postIds")
    void deleteByIds(@Param("postIds") List<Long> postIds);
}
