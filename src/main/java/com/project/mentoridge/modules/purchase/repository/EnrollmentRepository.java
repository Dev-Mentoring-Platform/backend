package com.project.mentoridge.modules.purchase.repository;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByMentee(Mentee mentee);
    Page<Enrollment> findByMentee(Mentee mentee, Pageable pageable);

    List<Enrollment> findByLecture(Lecture lecture);
    int countByLecture(Lecture lecture);
    Page<Enrollment> findByLecture(Lecture lecture, Pageable pageable);

    Optional<Enrollment> findByMenteeAndLectureAndLecturePrice(Mentee mentee, Lecture lecture, LecturePrice lecturePrice);

    // List<Enrollment> findByLectureAndLecturePrice(Lecture lecture, LecturePrice lecturePrice);
    @Query(value = "select e from Enrollment e where e.lecture.id = :lectureId and e.lecturePrice.id = :lecturePriceId")
    List<Enrollment> findAllByLectureIdAndLecturePriceId(@Param("lectureId") Long lectureId, @Param("lecturePriceId") Long lecturePriceId);

//    @Transactional
//    @Modifying
//    @Query(value = "delete from enrollment", nativeQuery = true)
//    void deleteAllEnrollments();

//    @Transactional
//    @Modifying
//    @Query(value = "delete from enrollment where enrollment_id = :enrollmentId", nativeQuery = true)
//    void deleteEnrollmentById(@Param("enrollmentId") Long enrollmentId);

    @Query(value = "select e from Enrollment e" +
            " join fetch e.lecture l" +
            " where e.id = :enrollmentId")
    Optional<Enrollment> findEnrollmentWithLectureByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    // ToOne 관계 - 페치 조인
    @Query(value = "select e from Enrollment e" +
            " join fetch e.lecture l" +
            " join fetch l.mentor t" +
            " where t.id = :mentorId")
    List<Enrollment> findAllWithLectureMentorByMentorId(@Param("mentorId") Long mentorId);
/*
    EXPLAIN
    SELECT COUNT(DISTINCT mentee_id) FROM enrollment e
    WHERE e.lecture_id IN (SELECT lecture_id FROM lecture WHERE mentor_id = 2 AND approved = 1)
    AND e.checked = 1*/
    @Query(value = "select count(distinct mentee_id) from enrollment e where e.lecture_id in (select lecture_id from lecture where mentor_id = :mentorId and approved = 1) and e.checked = 1", nativeQuery = true)
    int countAllMenteesByMentor(@Param("mentorId") Long mentorId);

//    SELECT count(*) FROM enrollment WHERE lecture_id IN (
//            SELECT lecture_id FROM lecture WHERE mentor_id = 10) and finished = 0
    @Query(value = "select count(*) from enrollment e where e.lecture_id in (select lecture_id from lecture where mentor_id = :mentorId and approved = 1) and e.finished = 0", nativeQuery = true)
    int countUnfinishedEnrollmentOfMentor(@Param("mentorId") Long mentorId);

    @Query(value = "select count(*) from enrollment e where e.checked = 1 and e.mentee_id = :menteeId and e.finished = 0", nativeQuery = true)
    int countUnfinishedEnrollmentOfMentee(@Param("menteeId") Long menteeId);

}
