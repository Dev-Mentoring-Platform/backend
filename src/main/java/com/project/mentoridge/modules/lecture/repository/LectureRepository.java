package com.project.mentoridge.modules.lecture.repository;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    List<Lecture> findByMentor(Mentor mentor);
    Page<Lecture> findByMentor(Mentor mentor, Pageable pageable);

    Optional<Lecture> findByMentorAndId(Mentor mentor, Long lectureId);

    // TODO - CHECK
    @Query(value = "select l from Lecture l inner join LecturePrice lp on l = lp.lecture where l.id = :lectureId and lp.id = :lecturePriceId")
    Lecture findByLectureIdAndLecturePriceId(@Param("lectureId") Long lectureId, @Param("lecturePriceId") Long lecturePriceId);
}
