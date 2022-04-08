package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.vo.Liking;
import com.project.mentoridge.modules.board.vo.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface LikingRepository extends JpaRepository<Liking, Long> {

//    List<Liking> findByUser(User user);
//    List<Liking> findByPost(Post post);

    Liking findByUserAndPost(User user, Post post);
}
