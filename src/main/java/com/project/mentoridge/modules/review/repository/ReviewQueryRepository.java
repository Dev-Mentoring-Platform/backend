package com.project.mentoridge.modules.review.repository;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.QUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.QLecture;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleLectureResponse;
import com.project.mentoridge.modules.review.vo.QReview;
import com.project.mentoridge.modules.review.vo.Review;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class ReviewQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QReview review = QReview.review;
    private final QUser user = QUser.user;
    private final QLecture lecture = QLecture.lecture;

    private final EntityManager em;

    public Page<Review> findReviewsWithUserByLecture(Lecture lecture, Pageable pageable) {

        QueryResults<Review> reviews = jpaQueryFactory.selectFrom(review)
                .innerJoin(review.user, user)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(eqLecture(lecture))
                .fetchResults();
        return new PageImpl<>(reviews.getResults(), pageable, reviews.getTotal());
    }

    // TODO - 제네릭 사용
    private BooleanExpression eqLecture(Lecture lecture) {
        if (lecture == null) {
            return null;
        }
        return review.lecture.eq(lecture);
    }

    private BooleanExpression eqUser(User user) {
        if (user == null) {
            return null;
        }
        return review.user.eq(user);
    }

    // SELECT * FROM review r LEFT OUTER JOIN review p ON r.review_id = p.parent_id
    // AND r.parent_id IS NULL
    public Page<ReviewResponse> findReviewsWithChildByLecture(Lecture lecture, Pageable pageable) {

        QueryResults<Review> parents = jpaQueryFactory.selectFrom(review)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(eqLecture(lecture), review.parent.isNull())
                .fetchResults();

        List<Long> parentIds = parents.getResults().stream().map(BaseEntity::getId).collect(Collectors.toList());
        List<Review> children = em.createQuery("select r from Review r where r.parent is not null and r.parent.id in :parentIds", Review.class)
                .setParameter("parentIds", parentIds).getResultList();

        Map<Long, Review> map = children.stream()
                .collect(Collectors.toMap(child -> child.getParent().getId(), child -> child));
        List<ReviewResponse> results = parents.getResults().stream()
                .map(parent -> new ReviewResponse(parent, map.get(parent.getId()))).collect(Collectors.toList());

        return new PageImpl<>(results, pageable, parents.getTotal());
    }

    // TODO - with User
//    public Page<ReviewResponse> findReviewsWithChildByUser(User user, Pageable pageable) {
//
//        QueryResults<Review> parents = jpaQueryFactory.selectFrom(review)
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .where(eqUser(user), review.parent.isNull())
//                .fetchResults();
//
//        List<Long> parentIds = parents.getResults().stream().map(BaseEntity::getId).collect(Collectors.toList());
//        List<Review> children = em.createQuery("select r from Review r where r.parent is not null and r.parent.id in :parentIds", Review.class)
//                .setParameter("parentIds", parentIds).getResultList();
//
//        Map<Long, Review> map = children.stream()
//                .collect(Collectors.toMap(child -> child.getParent().getId(), child -> child));
//        List<ReviewResponse> results = parents.getResults().stream()
//                .map(parent -> new ReviewResponse(parent, map.get(parent.getId()))).collect(Collectors.toList());
//
//        return new PageImpl<>(results, pageable, parents.getTotal());
//    }

    public Page<ReviewWithSimpleLectureResponse> findReviewsWithChildAndSimpleLectureByUser(User user, Pageable pageable) {

        QueryResults<Review> parents = jpaQueryFactory.selectFrom(review)
                .innerJoin(review.lecture, lecture)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(eqUser(user), review.parent.isNull())
                .fetchResults();

        List<Long> parentIds = parents.getResults().stream().map(BaseEntity::getId).collect(Collectors.toList());
        List<Review> children = em.createQuery("select r from Review r where r.parent is not null and r.parent.id in :parentIds", Review.class)
                .setParameter("parentIds", parentIds).getResultList();

        Map<Long, Review> map = children.stream()
                .collect(Collectors.toMap(child -> child.getParent().getId(), child -> child));
        List<ReviewWithSimpleLectureResponse> results = parents.getResults().stream()
                .map(parent -> new ReviewWithSimpleLectureResponse(parent, map.get(parent.getId()))).collect(Collectors.toList());

        return new PageImpl<>(results, pageable, parents.getTotal());
    }

    /*
    SELECT * FROM review r
    WHERE r.lecture_id IN (SELECT lecture_id FROM lecture WHERE mentor_id = 2 AND approved = 1)
    AND r.parent_id IS NULL;

    SELECT * FROM review r
    INNER JOIN lecture l ON r.lecture_id = l.lecture_id
    WHERE l.mentor_id = 2 AND l.approved = 1
    AND r.parent_id IS NULL;
    */
    public Page<ReviewWithSimpleLectureResponse> findReviewsWithSimpleLectureOfMentorByMentees(Mentor mentor, Pageable pageable) {

        QueryResults<Review> parents = jpaQueryFactory.selectFrom(review)
                .innerJoin(review.lecture, lecture)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(eqMentor(mentor), lecture.approved.isTrue(), review.parent.isNull())
                .fetchResults();

        List<ReviewWithSimpleLectureResponse> results = parents.getResults().stream()
                .map(ReviewWithSimpleLectureResponse::new).collect(Collectors.toList());
        return new PageImpl<>(results, pageable, parents.getTotal());
    }

    private BooleanExpression eqMentor(Mentor mentor) {
        if (mentor == null) {
            return null;
        }
        return lecture.mentor.eq(mentor);
    }
}
