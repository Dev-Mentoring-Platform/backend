package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface CareerRepository extends JpaRepository<Career, Long> {

    Optional<Career> findByMentorAndId(Mentor mentor, Long careerId);
    List<Career> findByMentor(Mentor mentor);
}
