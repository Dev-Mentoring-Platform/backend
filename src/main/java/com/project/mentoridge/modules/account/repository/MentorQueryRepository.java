package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.modules.account.controller.response.MenteeEnrollmentInfoResponse;
import com.project.mentoridge.modules.account.controller.response.MenteeSimpleResponse;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.QMentee;
import com.project.mentoridge.modules.account.vo.QMentor;
import com.project.mentoridge.modules.account.vo.QUser;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.QLecture;
import com.project.mentoridge.modules.lecture.vo.QLecturePrice;
import com.project.mentoridge.modules.purchase.vo.QEnrollment;
import com.project.mentoridge.modules.review.vo.QReview;
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
    private final QMentor mentor = QMentor.mentor;
    private final QMentee mentee = QMentee.mentee;
    private final QUser user = QUser.user;
    private final QEnrollment enrollment = QEnrollment.enrollment;
    private final QLecture lecture = QLecture.lecture;
    private final QLecturePrice lecturePrice = QLecturePrice.lecturePrice;

    private final QReview review = QReview.review;

    /*
    SELECT u.user_id, u.name, te.mentee_id FROM mentee te
    INNER JOIN user u ON te.user_id = u.user_id
    WHERE te.mentee_id IN
        (SELECT mentee_id FROM enrollment e WHERE e.lecture_id IN
            (SELECT lecture_id FROM lecture l WHERE l.mentor_id = 1));

    SELECT u.user_id, u.name, te.mentee_id, COUNT(l.lecture_id) FROM mentee te
    INNER JOIN user u ON te.user_id = u.user_id
    INNER JOIN enrollment e ON te.mentee_id = e.mentee_id
    INNER JOIN lecture l ON e.lecture_id = l.lecture_id
    WHERE l.mentor_id = 1
    GROUP BY u.user_id, u.name, te.mentee_id;

    SELECT e.mentee_id, count(e.lecture_id) FROM enrollment e
    INNER JOIN lecture l ON e.lecture_id = l.lecture_id
    WHERE l.mentor_id = 1
    GROUP BY e.mentee_id;
    */

    public Page<MenteeSimpleResponse> findMenteesOfMentor(Mentor mentor, Boolean closed, Pageable pageable) {

        // TODO - CHECK
        // TODO - 서브쿼리 효율성
        /*
        SELECT u.user_id, u.name, te.mentee_id FROM mentee te
        INNER JOIN user u ON te.user_id = u.user_id
        WHERE te.mentee_id IN
        (SELECT mentee_id FROM enrollment e WHERE e.lecture_id IN
        (SELECT lecture_id FROM lecture l WHERE l.mentor_id = 1));

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

        List<Long> lectureIds = jpaQueryFactory.select(lecture.id)
                .from(lecture)
                .where(lecture.mentor.eq(mentor))
                .fetch();
        List<Long> menteeIds = jpaQueryFactory.select(mentee.id)
                .from(enrollment)
                .where(enrollment.lecture.id.in(lectureIds))
                .fetch();

        QueryResults<Tuple> tuples = jpaQueryFactory.select(mentee.id, mentee.user.id, mentee.user.name)
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
                        .build())
                .collect(Collectors.toList());
        return new PageImpl<>(results, pageable, tuples.getTotal());
    }

    // TODO - CHECK
    public Page<MenteeEnrollmentInfoResponse> findMenteeLecturesOfMentor(Mentor mentor, Boolean closed, Long menteeId, Pageable pageable) {

        /*
            SELECT * FROM enrollment e
            INNER JOIN lecture_price lp ON e.lecture_price_id = lp.lecture_price_id
            INNER JOIN lecture l ON lp.lecture_id = l.lecture_id
            LEFT OUTER JOIN chatroom c ON e.enrollment_id = c.enrollment_id
            LEFT OUTER JOIN review r ON r.enrollment_id = e.enrollment_id
            WHERE e.mentee_id = 2 AND e.closed = 0 AND e.canceled = 0 AND l.mentor_id = 1;
        */

        QueryResults<Tuple> tuples = jpaQueryFactory.select(
                lecture, lecturePrice, review.id)
                .from(enrollment)
                .innerJoin(enrollment.lecturePrice, lecturePrice)
                .innerJoin(lecturePrice.lecture, lecture)
                .leftJoin(review).on(enrollment.eq(review.enrollment))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(enrollment.mentee.id.eq(menteeId),
                        lecture.mentor.eq(mentor))
                .fetchResults();

        List<MenteeEnrollmentInfoResponse> results = tuples.getResults().stream()
                .map(tuple -> MenteeEnrollmentInfoResponse.builder()
                        .menteeId(menteeId)
                        .lecture(tuple.get(0, Lecture.class))
                        .lecturePrice(tuple.get(1, LecturePrice.class))
                        .reviewId(tuple.get(2, Long.class))
                        .build()).collect(Collectors.toList());

        return new PageImpl<>(results, pageable, tuples.getTotal());
    }

}
