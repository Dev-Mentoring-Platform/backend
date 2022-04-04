package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.modules.board.vo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
