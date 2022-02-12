package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface MenteeRepository extends JpaRepository<Mentee, Long> {

    Mentee findByUser(User user);
}
