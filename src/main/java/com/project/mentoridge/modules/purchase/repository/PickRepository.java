package com.project.mentoridge.modules.purchase.repository;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.vo.Pick;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface PickRepository extends JpaRepository<Pick, Long> {

    List<Pick> findByMentee(Mentee mentee);
    Page<Pick> findByMentee(Mentee mentee, Pageable pageable);

    Optional<Pick> findByMenteeAndId(Mentee mentee, Long pickId);
    @Query(value = "select p from Pick p where p.mentee = :mentee and p.lecture.id = :lectureId and p.lecturePrice.id = :lecturePriceId")
    Optional<Pick> findByMenteeAndLectureIdAndLecturePriceId(@Param("mentee") Mentee mentee, @Param("lectureId") Long lectureId, @Param("lecturePriceId") Long lecturePriceId);

    @Transactional
    void deleteByMentee(Mentee mentee);

    @Transactional
    void deleteByLecture(Lecture lecture);

    List<Pick> findByLecture(Lecture lecture);
//    @Query(value = "select p from Pick p where p.lecture.id = :lectureId")
//    List<Pick> findByLectureId(@Param("lectureId") Long lectureId);

    Optional<Pick> findByMenteeAndLecture(Mentee mentee, Lecture lecture);

}
