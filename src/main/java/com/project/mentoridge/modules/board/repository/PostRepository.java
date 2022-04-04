package com.project.mentoridge.modules.board.repository;


import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.vo.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUserAndId(User user, Long postId);
    Page<Post> findByUser(User user, Pageable pageable);
}
