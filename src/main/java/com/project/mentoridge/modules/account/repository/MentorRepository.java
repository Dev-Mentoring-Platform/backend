package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface MentorRepository extends JpaRepository<Mentor, Long> {

    // Optional<Mentor> findByUser(User user);
    Mentor findByUser(User user);
}
