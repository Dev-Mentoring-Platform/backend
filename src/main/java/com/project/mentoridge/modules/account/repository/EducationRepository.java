package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface EducationRepository extends JpaRepository<Education, Long> {

    Optional<Education> findByMentorAndId(Mentor mentor, Long educationId);
    List<Education> findByMentor(Mentor mentor);
}
