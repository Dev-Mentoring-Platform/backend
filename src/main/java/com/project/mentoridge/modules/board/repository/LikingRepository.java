package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.vo.Liking;
import com.project.mentoridge.modules.board.vo.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface LikingRepository extends JpaRepository<Liking, Long> {

    List<Liking> findByUser(User user);
    List<Liking> findByPost(Post post);
    Liking findByUserAndPost(User user, Post post);

    @Transactional
    @Modifying
    void deleteByUser(User user);

    @Transactional
    @Modifying
    void deleteByPost(Post post);

    @Transactional
    @Modifying
    @Query(value = "delete from Liking l where l.post.id in :postIds")
    void deleteByPostIds(@Param("postIds") List<Long> postIds);

}
