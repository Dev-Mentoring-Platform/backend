package com.project.mentoridge.modules.lecture.repository;

import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface LectureSubjectRepository extends JpaRepository<LectureSubject, Long> {

    List<LectureSubject> findByLecture(Lecture lecture);

    @Query(value = "select * from lecture_subject where lecture_id = :lectureId", nativeQuery = true)
    List<LectureSubject> findByLectureId(@Param("lectureId") Long lectureId);

    @Query(value = "select * from lecture_subject where lecture_id in :lectureIds", nativeQuery = true)
    List<LectureSubject> findByLectureIds(@Param("lectureIds") List<Long> lectureIds);
}
