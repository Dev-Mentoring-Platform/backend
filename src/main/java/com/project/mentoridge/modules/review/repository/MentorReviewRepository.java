package com.project.mentoridge.modules.review.repository;

import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface MentorReviewRepository extends JpaRepository<MentorReview, Long> {

    Optional<MentorReview> findByParentAndId(MenteeReview parent, Long mentorReviewId);
    Optional<MentorReview> findByParent(MenteeReview parent);

    @Transactional
    void deleteByParent(MenteeReview parent);
}
