package com.project.mentoridge.modules.review.repository;

import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface MentorReviewRepository extends JpaRepository<MentorReview, Long> {

    Optional<MentorReview> findByParentAndId(MenteeReview parent, Long mentorReviewId);

    @Query(value = "select r from MentorReview r join fetch r.mentor m join fetch m.user u where r.parent = :parent")
    Optional<MentorReview> findByParent(@Param("parent") MenteeReview parent);

    @Transactional
    void deleteByParent(MenteeReview parent);
}
