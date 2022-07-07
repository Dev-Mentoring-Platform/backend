package com.project.mentoridge.modules.purchase.repository;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.lecture.vo.QLecture;
import com.project.mentoridge.modules.lecture.vo.QLecturePrice;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithEachLectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleEachLectureResponse;
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

    public Page<EnrollmentWithSimpleEachLectureResponse> findEnrollments(Mentee mentee, boolean reviewed, Pageable pageable) {

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

        List<EnrollmentWithSimpleEachLectureResponse> results = enrollments.getResults().stream().map(EnrollmentWithSimpleEachLectureResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, enrollments.getTotal());
    }

    public EnrollmentWithSimpleEachLectureResponse findEnrollment(Mentee mentee, Long enrollmentId) {

        Enrollment enrollment = jpaQueryFactory.selectFrom(this.enrollment)
                .innerJoin(this.enrollment.lecturePrice, lecturePrice)
                .fetchJoin()
                .innerJoin(lecturePrice.lecture, lecture)
                .fetchJoin()
                .where(this.enrollment.id.eq(enrollmentId))
                .fetchOne();
        return new EnrollmentWithSimpleEachLectureResponse(enrollment);
    }

    public Page<EnrollmentWithEachLectureResponse> findEnrollmentsWithEachLecture(Mentee mentee, boolean checked, Pageable pageable) {

        QueryResults<Enrollment> enrollments = jpaQueryFactory.selectFrom(enrollment)
                .innerJoin(enrollment.lecturePrice, lecturePrice)
                .fetchJoin()
                .innerJoin(enrollment.lecture, lecture)
                .fetchJoin()
                .where(enrollment.mentee.eq(mentee), enrollment.checked.eq(checked))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<EnrollmentWithEachLectureResponse> results = enrollments.getResults()
                .stream().map(EnrollmentWithEachLectureResponse::new).collect(Collectors.toList());
        return new PageImpl<>(results, pageable, enrollments.getTotal());
    }

    public EachLectureResponse findEachLectureOfEnrollment(Mentee mentee, Long enrollmentId, boolean checked) {

        Enrollment enrollment = jpaQueryFactory.selectFrom(this.enrollment)
                .innerJoin(this.enrollment.lecturePrice, lecturePrice)
                .fetchJoin()
                .innerJoin(this.enrollment.lecture, lecture)
                .fetchJoin()
                .where(this.enrollment.id.eq(enrollmentId), this.enrollment.checked.eq(checked))
                .fetchOne();

        return new EachLectureResponse(enrollment.getLecturePrice(), enrollment.getLecture());
    }

}
