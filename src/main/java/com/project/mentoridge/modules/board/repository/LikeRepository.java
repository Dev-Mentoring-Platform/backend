package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.vo.Like;
import com.project.mentoridge.modules.board.vo.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface LikeRepository extends JpaRepository<Like, Long> {

//    List<Like> findByUser(User user);
//    List<Like> findByPost(Post post);

    Like findByUserAndPost(User user, Post post);
}
