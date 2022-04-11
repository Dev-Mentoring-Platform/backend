package com.project.mentoridge.modules.lecture.repository;

import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface LecturePriceRepository extends JpaRepository<LecturePrice, Long> {

    List<LecturePrice> findByLecture(Lecture lecture);

    @Query(value = "select * from lecture_price where lecture_id = :lectureId", nativeQuery = true)
    List<LecturePrice> findByLectureId(@Param("lectureId") Long lectureId);

    Optional<LecturePrice> findByLectureAndId(Lecture lecture, Long lecturePriceId);
}
