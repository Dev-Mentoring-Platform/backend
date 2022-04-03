package com.project.mentoridge.modules.review.repository;

import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.vo.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByLecture(Lecture lecture);
    // Page<Review> findByLecture(Lecture lecture, Pageable pageable);

    Review findByEnrollment(Enrollment enrollment);

    List<Review> findByLectureAndEnrollmentIsNotNull(Lecture lecture);

    // TODO - CHECK
    Optional<Review> findByLectureAndId(Lecture lecture, Long reviewId);
    Optional<Review> findByEnrollmentAndId(Enrollment enrollment, Long reviewId);

    Optional<Review> findByParent(Review parent);
    Optional<Review> findByParentAndId(Review parent, Long reviewId);

    // TODO - CHECK : 쿼리
    int countByLectureInAndEnrollmentIsNotNull(List<Lecture> lectures);

    @Query(value = "select r from Review r join fetch r.lecture l where r.id = :reviewId")
    Optional<Review> findWithLectureByReviewId(Long reviewId);
}
