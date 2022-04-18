package com.project.mentoridge.modules.review.repository;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.QMentee;
import com.project.mentoridge.modules.account.vo.QMentor;
import com.project.mentoridge.modules.account.vo.QUser;
import com.project.mentoridge.modules.lecture.vo.QLecture;
import com.project.mentoridge.modules.review.controller.response.ReviewListResponse;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleLectureResponse;
import com.project.mentoridge.modules.review.vo.MenteeReview;
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

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class MentorReviewQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QMenteeReview menteeReview = QMenteeReview.menteeReview;
    private final QLecture lecture = QLecture.lecture;
    private final QMentor mentor = QMentor.mentor;
    private final QMentee mentee = QMentee.mentee;
    private final QUser user  = QUser.user;

    public Page<ReviewWithSimpleLectureResponse> findReviewsWithSimpleLectureOfMentorByMentees(Mentor _mentor, Pageable pageable) {

        QueryResults<MenteeReview> parents = jpaQueryFactory.selectFrom(menteeReview)
                .innerJoin(menteeReview.lecture, lecture)
                .fetchJoin()
                .innerJoin(lecture.mentor, mentor)
                .fetchJoin()
                .innerJoin(mentor.user, user)
                .fetchJoin()
                .innerJoin(menteeReview.mentee, mentee)
                .fetchJoin()
                .innerJoin(mentee.user, user)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(eqMentor(_mentor), lecture.approved.isTrue())
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

    public ReviewListResponse findReviewsOfMentorByMentees(Mentor _mentor, Pageable pageable) {

        QueryResults<MenteeReview> reviews = jpaQueryFactory.selectFrom(menteeReview)
                .innerJoin(menteeReview.lecture, lecture)
                .fetchJoin()
                .innerJoin(lecture.mentor, mentor)
                .fetchJoin()
                .innerJoin(mentor.user, user)
                .fetchJoin()
                .innerJoin(menteeReview.mentee, mentee)
                .fetchJoin()
                .innerJoin(mentee.user, user)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(eqMentor(_mentor), lecture.approved.isTrue())
                .fetchResults();

        double scoreAverage = reviews.getResults().stream().mapToInt(MenteeReview::getScore).average().getAsDouble();
        List<ReviewWithSimpleLectureResponse> results = reviews.getResults().stream()
                .map(ReviewWithSimpleLectureResponse::new).collect(Collectors.toList());

        return new ReviewListResponse(scoreAverage, new PageImpl<>(results, pageable, reviews.getTotal()), reviews.getTotal());
    }
}
