package com.project.mentoridge.modules.review.repository;

import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MenteeReviewRepository extends JpaRepository<MenteeReview, Long> {

    Optional<MenteeReview> findByLectureAndId(Lecture lecture, Long menteeReviewId);
    Optional<MenteeReview> findByEnrollmentAndId(Enrollment enrollment, Long menteeReviewId);

    @Query(value = "select r from MenteeReview r" +
            " join fetch r.enrollment e" +
            " join fetch e.lecturePrice lp" +
            " join fetch r.lecture l" +
            " where r.id = :menteeReviewId")
    MenteeReview findByMenteeReviewId(@Param("menteeReviewId") Long menteeReviewId);

    MenteeReview findByEnrollment(Enrollment enrollment);
    List<MenteeReview> findByLecture(Lecture lecture);
    int countByLectureIn(List<Lecture> lectures);
}
