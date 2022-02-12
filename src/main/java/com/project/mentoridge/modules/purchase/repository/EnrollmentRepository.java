package com.project.mentoridge.modules.purchase.repository;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
// TODO - CHECK
    @Query(value = "select * from enrollment where enrollment_id = :enrollmentId", nativeQuery = true)
    Enrollment findAllById(Long enrollmentId);

    List<Enrollment> findByMenteeAndCanceledFalseAndClosedFalse(Mentee mentee);
    @Query(value = "select * from enrollment where mentee_id = :menteeId", nativeQuery = true)
    List<Enrollment> findAllByMenteeId(Long menteeId);
    Page<Enrollment> findByMenteeAndCanceledFalseAndClosedFalse(Mentee mentee, Pageable pageable);

    @Query(value = "select * from enrollment where lecture_id = :lectureId", nativeQuery = true)
    List<Enrollment> findAllByLectureId(Long lectureId);
    @Query(value = "select count(*) from enrollment where lecture_id = :lectureId", nativeQuery = true)
    Integer countAllByLectureId(Long lectureId);

    Page<Enrollment> findByLectureAndCanceledFalseAndClosedFalse(Lecture lecture, Pageable pageable);

    // Optional<Enrollment> findByLectureAndIdAndCanceledFalseAndClosedFalse(Lecture lecture, Long enrollmentId);
    Optional<Enrollment> findByMenteeAndLectureAndCanceledFalseAndClosedFalse(Mentee mentee, Lecture lecture);

    @Query(value = "select * from enrollment where mentee_id = :menteeId and lecture_id = :lectureId", nativeQuery = true)
    Optional<Enrollment> findAllByMenteeIdAndLectureId(Long menteeId, Long lectureId);

    @Transactional
    @Modifying
    @Query(value = "delete from enrollment", nativeQuery = true)
    void deleteAllEnrollments();

    @Transactional
    @Modifying
    @Query(value = "delete from enrollment where enrollment_id = :enrollmentId", nativeQuery = true)
    void deleteEnrollmentById(Long enrollmentId);

    // ToOne 관계 - 페치 조인
    // and (e.closed = false and e.canceled = false)
    @Query(value = "select e from Enrollment e" +
            " join fetch e.lecture l" +
            " join fetch l.mentor t" +
            " where t.id = :mentorId and (e.closed = false and e.canceled = false)")
    List<Enrollment> findAllWithLectureMentorByMentorId(Long mentorId);
}
