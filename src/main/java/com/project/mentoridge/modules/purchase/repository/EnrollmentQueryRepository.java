package com.project.mentoridge.modules.purchase.repository;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.lecture.vo.QLecture;
import com.project.mentoridge.modules.lecture.vo.QLecturePrice;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.purchase.vo.QEnrollment;
import com.project.mentoridge.modules.review.vo.QMenteeReview;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
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
public class EnrollmentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QEnrollment enrollment = QEnrollment.enrollment;
    private final QMenteeReview menteeReview = QMenteeReview.menteeReview;

    private final QLecturePrice lecturePrice = QLecturePrice.lecturePrice;
    private final QLecture lecture = QLecture.lecture;

    // SELECT * FROM enrollment e
    // INNER JOIN lecture_price lp ON e.lecture_price_id = lp.lecture_price_id
    // INNER JOIN lecture l ON e.lecture_id = l.lecture_id
    // WHERE EXISTS (SELECT enrollment_id, lecture_id FROM review r WHERE e.enrollment_id = r.enrollment_id AND e.lecture_id = r.lecture_id)
    public Page<EnrollmentWithSimpleLectureResponse> findEnrollments(Mentee mentee, boolean reviewed, Pageable pageable) {

        QueryResults<Enrollment> enrollments = jpaQueryFactory.selectFrom(enrollment)
                .innerJoin(enrollment.lecturePrice, lecturePrice)
                .fetchJoin()
                .innerJoin(lecturePrice.lecture, lecture)
                .fetchJoin()
                .where(reviewed ? JPAExpressions.selectFrom(menteeReview).where(menteeReview.enrollment.eq(enrollment)).exists() : JPAExpressions.selectFrom(menteeReview).where(menteeReview.enrollment.eq(enrollment)).notExists(),
                        enrollment.mentee.eq(mentee), enrollment.checked.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<EnrollmentWithSimpleLectureResponse> results = enrollments.getResults().stream().map(EnrollmentWithSimpleLectureResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, enrollments.getTotal());
    }

}
