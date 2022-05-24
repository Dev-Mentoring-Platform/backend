package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.modules.account.controller.response.MenteeEnrollmentInfoResponse;
import com.project.mentoridge.modules.account.controller.response.MenteeSimpleResponse;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.QMentee;
import com.project.mentoridge.modules.account.vo.QUser;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.QLecture;
import com.project.mentoridge.modules.lecture.vo.QLecturePrice;
import com.project.mentoridge.modules.purchase.vo.QEnrollment;
import com.project.mentoridge.modules.review.vo.QMenteeReview;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
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
public class MentorQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QMentee mentee = QMentee.mentee;
    private final QUser user = QUser.user;
    private final QEnrollment enrollment = QEnrollment.enrollment;
    private final QLecture lecture = QLecture.lecture;
    private final QLecturePrice lecturePrice = QLecturePrice.lecturePrice;
    private final QMenteeReview menteeReview = QMenteeReview.menteeReview;


    // TODO - CHECK
    // TODO - 서브쿼리 효율성
    /*
    QueryResults<Tuple> mentees = jpaQueryFactory.select(mentee.id, mentee.user.id, mentee.user.name)
            .from(mentee)
            .innerJoin(mentee.user, user)
            //.fetchJoin()
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .where(mentee.id.in(
                    JPAExpressions.select(mentee.id).from(enrollment).where(enrollment.lecture.id.in(
                            JPAExpressions.select(lecture.id).from(lecture).where(lecture.mentor.eq(mentor))))))
            .fetchResults();
    */

    public Page<MenteeSimpleResponse> findMenteesOfMentor(Mentor mentor, Boolean closed, Pageable pageable) {

        List<Long> lectureIds = jpaQueryFactory.select(lecturePrice.id)
                .from(lecturePrice)
                .where(lecturePrice.lecture.approved.eq(true),
                        lecturePrice.lecture.mentor.eq(mentor), lecturePrice.closed.eq(closed))
                .fetch();
        List<Long> menteeIds = jpaQueryFactory.select(mentee.id)
                .from(enrollment)
                .where(enrollment.lecturePrice.id.in(lectureIds))
                .fetch();

        QueryResults<Tuple> tuples = jpaQueryFactory.select(mentee.id, mentee.user.id, mentee.user.name, mentee.user.nickname)
                .from(mentee)
                .innerJoin(mentee.user, user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(mentee.id.in(menteeIds))
                .fetchResults();

        List<MenteeSimpleResponse> results = tuples.getResults().stream()
                .map(tuple -> MenteeSimpleResponse.builder()
                        .menteeId(tuple.get(0, Long.class))
                        .userId(tuple.get(1, Long.class))
                        .name(tuple.get(2, String.class))
                        .nickname(tuple.get(3, String.class))
                        .build())
                .collect(Collectors.toList());
        return new PageImpl<>(results, pageable, tuples.getTotal());
    }

    // TODO - CHECK
    public Page<MenteeEnrollmentInfoResponse> findMenteeLecturesOfMentor(Mentor mentor, Long menteeId, Pageable pageable) {

        QueryResults<Tuple> tuples = jpaQueryFactory.select(
                enrollment.id, lecture, lecturePrice, menteeReview.id)
                .from(enrollment)
                .innerJoin(enrollment.lecturePrice, lecturePrice)
                .innerJoin(lecturePrice.lecture, lecture)
                .leftJoin(menteeReview).on(enrollment.eq(menteeReview.enrollment))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(enrollment.mentee.id.eq(menteeId),
                        lecture.mentor.eq(mentor))
                .fetchResults();

        List<MenteeEnrollmentInfoResponse> results = tuples.getResults().stream()
                .map(tuple -> MenteeEnrollmentInfoResponse.builder()
                        .menteeId(menteeId)
                        .enrollmentId(tuple.get(0, Long.class))
                        .lecture(tuple.get(1, Lecture.class))
                        .lecturePrice(tuple.get(2, LecturePrice.class))
                        .reviewId(tuple.get(3, Long.class))
                        .build()).collect(Collectors.toList());

        return new PageImpl<>(results, pageable, tuples.getTotal());
    }

}
