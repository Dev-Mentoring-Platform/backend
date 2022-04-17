package com.project.mentoridge.modules.lecture.repository;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    List<Lecture> findByMentor(Mentor mentor);
    Page<Lecture> findByMentor(Mentor mentor, Pageable pageable);

    Optional<Lecture> findByMentorAndId(Mentor mentor, Long lectureId);
}
