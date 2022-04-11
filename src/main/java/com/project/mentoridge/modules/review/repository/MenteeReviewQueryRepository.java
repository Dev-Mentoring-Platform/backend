package com.project.mentoridge.modules.review.repository;

import com.project.mentoridge.modules.account.vo.QMentee;
import com.project.mentoridge.modules.account.vo.QUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.QLecture;
import com.project.mentoridge.modules.lecture.vo.QLecturePrice;
import com.project.mentoridge.modules.purchase.vo.QEnrollment;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleLectureResponse;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import com.project.mentoridge.modules.review.vo.QMenteeReview;
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
public class MenteeReviewQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QMenteeReview menteeReview = QMenteeReview.menteeReview;
    private final QMentee mentee = QMentee.mentee;
    private final QUser user = QUser.user;
    private final QLecture lecture = QLecture.lecture;
    private final QLecturePrice lecturePrice = QLecturePrice.lecturePrice;
    private final QEnrollment enrollment = QEnrollment.enrollment;

    private final EntityManager em;

    public Page<MenteeReview> findReviewsWithUserByLecture(Lecture lecture, Pageable pageable) {

        QueryResults<MenteeReview> reviews = jpaQueryFactory.selectFrom(menteeReview)
                .innerJoin(menteeReview.mentee, mentee)
                .fetchJoin()
                .innerJoin(mentee.user, user)
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
        return menteeReview.lecture.eq(lecture);
    }

    private BooleanExpression eqUser(User user) {
        if (user == null) {
            return null;
        }
        return menteeReview.mentee.user.eq(user);
    }

        private Map<Long, MentorReview> getChildren(QueryResults<MenteeReview> parents) {
            List<Long> parentIds = parents.getResults().stream().map(BaseEntity::getId).collect(Collectors.toList());
            List<MentorReview> children = em.createQuery("select or from MentorReview or where or.parent.id in :parentIds", MentorReview.class)
                    .setParameter("parentIds", parentIds).getResultList();

            Map<Long, MentorReview> map = children.stream()
                    .collect(Collectors.toMap(child -> child.getParent().getId(), child -> child));
            return map;
        }

    public Page<ReviewResponse> findReviewsWithChildByLecture(Lecture lecture, Pageable pageable) {

        QueryResults<MenteeReview> parents = jpaQueryFactory.selectFrom(menteeReview)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(eqLecture(lecture))
                .fetchResults();

        Map<Long, MentorReview> map = getChildren(parents);
        List<ReviewResponse> results = parents.getResults().stream()
                .map(parent -> new ReviewResponse(parent, map.get(parent.getId()))).collect(Collectors.toList());
        return new PageImpl<>(results, pageable, parents.getTotal());
    }

    public Page<ReviewWithSimpleLectureResponse> findReviewsWithChildAndSimpleLectureByUser(User user, Pageable pageable) {

        QueryResults<MenteeReview> parents = jpaQueryFactory.selectFrom(menteeReview)
                .innerJoin(menteeReview.enrollment, enrollment)
                .fetchJoin()
                .innerJoin(enrollment.lecturePrice, lecturePrice)
                .fetchJoin()
//                .innerJoin(lecturePrice.lecture, lecture)
//                .fetchJoin()
                .innerJoin(menteeReview.lecture, lecture)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(eqUser(user), lecture.approved.isTrue())
                .fetchResults();

        Map<Long, MentorReview> map = getChildren(parents);
        List<ReviewWithSimpleLectureResponse> results = parents.getResults().stream()
                .map(parent -> new ReviewWithSimpleLectureResponse(parent, map.get(parent.getId()))).collect(Collectors.toList());

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
}
