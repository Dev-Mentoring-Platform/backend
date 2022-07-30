package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.modules.account.controller.response.MenteeEnrollmentInfoResponse;
import com.project.mentoridge.modules.account.controller.response.SimpleMenteeResponse;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.QMentee;
import com.project.mentoridge.modules.account.vo.QUser;
import com.project.mentoridge.modules.lecture.controller.response.LectureSubjectResponse;
import com.project.mentoridge.modules.lecture.repository.LectureSubjectRepository;
import com.project.mentoridge.modules.lecture.vo.*;
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
import java.util.Map;
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

    private final LectureSubjectRepository lectureSubjectRepository;

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

    public Page<SimpleMenteeResponse> findMenteesOfMentor(Mentor mentor, Boolean closed, Boolean checked, Pageable pageable) {

        List<Long> lecturePriceIds = jpaQueryFactory.select(lecturePrice.id)
                .from(lecturePrice)
                .where(lecturePrice.lecture.approved.eq(true),
                        lecturePrice.lecture.mentor.eq(mentor), lecturePrice.closed.eq(closed))
                .fetch();
        List<Long> enrollmentIds = jpaQueryFactory.select(enrollment.id)
                .from(enrollment)
                .where(enrollment.lecturePrice.id.in(lecturePriceIds), enrollment.checked.eq(checked))
                .fetch();

        QueryResults<Tuple> tuples = jpaQueryFactory.select(mentee.id, mentee.user.id, mentee.user.name, mentee.user.nickname, enrollment.id)
                .from(enrollment)
                .innerJoin(enrollment.mentee, mentee)
                .innerJoin(mentee.user, user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(enrollment.id.in(enrollmentIds))
                .fetchResults();

        List<SimpleMenteeResponse> results = tuples.getResults().stream()
                .map(tuple -> SimpleMenteeResponse.builder()
                        .menteeId(tuple.get(0, Long.class))
                        .userId(tuple.get(1, Long.class))
                        .name(tuple.get(2, String.class))
                        .nickname(tuple.get(3, String.class))
                        .enrollmentId(tuple.get(4, Long.class))
                        .build())
                .collect(Collectors.toList());
        return new PageImpl<>(results, pageable, tuples.getTotal());
    }

    public List<SimpleMenteeResponse> findMenteesOfMentor(Mentor mentor, Boolean closed, Boolean checked) {

        List<Long> lecturePriceIds = jpaQueryFactory.select(lecturePrice.id)
                .from(lecturePrice)
                .where(lecturePrice.lecture.approved.eq(true),
                        // TEST
                        lecturePrice.lecture.mentor.eq(mentor), (closed != null ? lecturePrice.closed.eq(closed) : null))
                .fetch();
        List<Long> enrollmentIds = jpaQueryFactory.select(enrollment.id)
                .from(enrollment)
                .where(enrollment.lecturePrice.id.in(lecturePriceIds), enrollment.checked.eq(checked))
                .fetch();

        QueryResults<Tuple> tuples = jpaQueryFactory.select(mentee.id, mentee.user.id, mentee.user.name, mentee.user.nickname, enrollment.id)
                .from(enrollment)
                .innerJoin(enrollment.mentee, mentee)
                .innerJoin(mentee.user, user)
                .where(enrollment.id.in(enrollmentIds))
                .fetchResults();

        return tuples.getResults().stream()
                .map(tuple -> SimpleMenteeResponse.builder()
                        .menteeId(tuple.get(0, Long.class))
                        .userId(tuple.get(1, Long.class))
                        .name(tuple.get(2, String.class))
                        .nickname(tuple.get(3, String.class))
                        .enrollmentId(tuple.get(4, Long.class))
                        .build())
                .collect(Collectors.toList());
    }

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
        List<Long> lectureIds = results.stream().map(result -> result.getLecture().getLectureId()).collect(Collectors.toList());
        Map<Long, List<LectureSubject>> map = lectureSubjectRepository.findByLectureIds(lectureIds).stream()
                .collect(Collectors.groupingBy(lectureSubject -> lectureSubject.getLecture().getId()));
        for(MenteeEnrollmentInfoResponse result : results) {
            List<LectureSubjectResponse> lectureSubjects = map.get(result.getLecture().getLectureId()).stream()
                    .map(LectureSubjectResponse::new).collect(Collectors.toList());
            result.getLecture().setLectureSubjects(lectureSubjects);
        }
        return new PageImpl<>(results, pageable, tuples.getTotal());
    }

    public MenteeEnrollmentInfoResponse findMenteeLectureOfMentor(Mentor mentor, Long menteeId, Long enrollmentId) {

        Tuple tuple = jpaQueryFactory.select(enrollment.id, lecture, lecturePrice, menteeReview.id)
                .from(enrollment)
                .innerJoin(enrollment.lecturePrice, lecturePrice)
                .innerJoin(lecturePrice.lecture, lecture)
                .leftJoin(menteeReview).on(enrollment.eq(menteeReview.enrollment))
                .where(enrollment.mentee.id.eq(menteeId),
                        enrollment.id.eq(enrollmentId),
                        lecture.mentor.eq(mentor))
                .fetchOne();

        if (tuple != null) {
            MenteeEnrollmentInfoResponse response = MenteeEnrollmentInfoResponse.builder()
                    .menteeId(menteeId)
                    .enrollmentId(tuple.get(0, Long.class))
                    .lecture(tuple.get(1, Lecture.class))
                    .lecturePrice(tuple.get(2, LecturePrice.class))
                    .reviewId(tuple.get(3, Long.class))
                    .build();
            Long lectureId = response.getLecture().getLectureId();
            List<LectureSubjectResponse> lectureSubjects = lectureSubjectRepository.findByLectureId(lectureId).stream()
                    .map(LectureSubjectResponse::new).collect(Collectors.toList());
            response.getLecture().setLectureSubjects(lectureSubjects);
            return response;
        }
        return null;
    }

}
