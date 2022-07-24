package com.project.mentoridge.modules.lecture.repository;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.QMentor;
import com.project.mentoridge.modules.account.vo.QUser;
import com.project.mentoridge.modules.address.embeddable.Address;
import com.project.mentoridge.modules.lecture.controller.request.LectureListRequest;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.QLecture;
import com.project.mentoridge.modules.lecture.vo.QLecturePrice;
import com.project.mentoridge.modules.purchase.vo.QEnrollment;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class LectureSearchRepository {
    // TODO - 테스트
    private final JPAQueryFactory jpaQueryFactory;
    private final QLecture lecture = QLecture.lecture;
    private final QLecturePrice lecturePrice = QLecturePrice.lecturePrice;

    private final QMentor mentor = QMentor.mentor;
    private final QUser user = QUser.user;
    private final QEnrollment enrollment = QEnrollment.enrollment;

    public Page<LectureResponse> findLecturesWithEnrollmentCountByMentor(Mentor mentor, Pageable pageable) {

        QueryResults<Tuple> tuples = jpaQueryFactory.select(lecture,
                JPAExpressions.select(enrollment.id.count()).from(enrollment).where(lecture.eq(enrollment.lecture)))
                .from(lecture)
                .where(lecture.mentor.eq(mentor))
                .fetchResults();
        List<LectureResponse> lectureResponses = tuples.getResults().stream().map(tuple -> {
            LectureResponse lectureResponse = new LectureResponse(tuple.get(0, Lecture.class));
            lectureResponse.setEnrollmentCount(tuple.get(1, Long.class));
            return lectureResponse;
        }).collect(Collectors.toList());

        return new PageImpl<>(lectureResponses, pageable, tuples.getTotal());
    }

    private BooleanExpression eqApproved(boolean approved) {
        return lecture.approved.eq(approved);
    }

    private BooleanExpression eqClosed(boolean closed) {
        return lecturePrice.closed.eq(closed);
    }

    private BooleanExpression eqState(String state) {
        if (StringUtils.isBlank(state)) {
            return null;
        }
        return user.zone.state.eq(state);
    }

    private BooleanExpression eqSiGunGu(String siGunGu) {
        if (StringUtils.isBlank(siGunGu)) {
            return null;
        }
        return user.zone.siGunGu.eq(siGunGu);
    }

    public Page<LecturePrice> findLecturePricesByZoneAndSearch(Address zone, LectureListRequest request, Pageable pageable) {

        QueryResults<LecturePrice> lecturePrices;
        if (zone != null && request != null) {

            lecturePrices = jpaQueryFactory.selectFrom(lecturePrice)
                    .innerJoin(lecturePrice.lecture, lecture)
                    .fetchJoin()
                    .innerJoin(lecture.mentor, mentor)
                    .fetchJoin()
                    .innerJoin(mentor.user, user)
                    .fetchJoin()
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .where(eqState(zone.getState()),
                            eqSiGunGu(zone.getSiGunGu()),
                            eqTitle(request.getTitle()),
                            inSubjects(request.getSubjects()),
                            eqSystemType(request.getSystemType()),
                            eqIsGroup(request.getIsGroup()),
                            inDifficultyType(request.getDifficultyTypes()),
                            eqApproved(true),
                            eqClosed(false))
                    .orderBy(lecturePrice.id.asc())
                    .fetchResults();

        } else if (zone == null) {

            lecturePrices = jpaQueryFactory.selectFrom(lecturePrice)
                    .innerJoin(lecturePrice.lecture, lecture)
                    .fetchJoin()
                    .innerJoin(lecture.mentor, mentor)
                    .fetchJoin()
                    .innerJoin(mentor.user, user)
                    .fetchJoin()
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .where(eqTitle(request.getTitle()),
                            inSubjects(request.getSubjects()),
                            eqSystemType(request.getSystemType()),
                            eqIsGroup(request.getIsGroup()),
                            inDifficultyType(request.getDifficultyTypes()),
                            eqApproved(true),
                            eqClosed(false))
                    .orderBy(lecturePrice.id.asc())
                    .fetchResults();

        } else if (request == null) {

            lecturePrices = jpaQueryFactory.selectFrom(lecturePrice)
                    .innerJoin(lecturePrice.lecture, lecture)
                    .fetchJoin()
                    .innerJoin(lecture.mentor, mentor)
                    .fetchJoin()
                    .innerJoin(mentor.user, user)
                    .fetchJoin()
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .where(eqState(zone.getState()),
                            eqSiGunGu(zone.getSiGunGu()),
                            eqApproved(true),
                            eqClosed(false))
                    .orderBy(lecturePrice.id.asc())
                    .fetchResults();

        } else {

            lecturePrices = jpaQueryFactory.selectFrom(lecturePrice)
                    .innerJoin(lecturePrice.lecture, lecture)
                    .fetchJoin()
                    .innerJoin(lecture.mentor, mentor)
                    .fetchJoin()
                    .innerJoin(mentor.user, user)
                    .fetchJoin()
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .where(eqApproved(true),
                            eqClosed(false))
                    .orderBy(lecturePrice.id.asc())
                    .fetchResults();
        }

        return new PageImpl<>(lecturePrices.getResults(), pageable, lecturePrices.getTotal());
    }

    // TODO - 제네릭 사용해서 util로 변경
    private BooleanExpression eqTitle(String title) {
        if (StringUtils.isBlank(title)) {
            return null;
        }
        return lecture.title.eq(title);
    }

    private BooleanExpression startsWithTitle(String title) {
        if (StringUtils.isBlank(title)) {
            return null;
        }
        return lecture.title.startsWith(title);
    }

    private BooleanExpression inSubjects(List<String> subjects) {
        if (CollectionUtils.isEmpty(subjects)) {
            return null;
        }
        return lecture.lectureSubjects.any().subject.krSubject.in(subjects);
    }

    private BooleanExpression eqSystemType(SystemType systemType) {
        if (Objects.isNull(systemType)) {
            return null;
        }
        return lecture.systems.contains(systemType);
    }

    private BooleanExpression eqIsGroup(boolean isGroup) {
        return lecturePrice.isGroup.eq(isGroup);
        // return lecture.lecturePrices.any().isGroup.eq(isGroup);
    }

    private BooleanExpression inDifficultyType(List<DifficultyType> difficultyTypes) {
        if (CollectionUtils.isEmpty(difficultyTypes)) {
            return null;
        }
        return lecture.difficulty.in(difficultyTypes);
    }

    private BooleanExpression eqMentor(Mentor mentor) {
        if (Objects.isNull(mentor)) {
            return null;
        }
        return lecture.mentor.eq(mentor);
    }

    public Page<LecturePrice> findLecturePricesByMentor(Mentor _mentor, Pageable pageable) {

        QueryResults<LecturePrice> lecturePrices = jpaQueryFactory.selectFrom(lecturePrice)
                .innerJoin(lecturePrice.lecture, lecture)
                .fetchJoin()
                .innerJoin(lecture.mentor, mentor)
                .fetchJoin()
                .innerJoin(mentor.user, user)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(eqMentor(_mentor),
                        eqApproved(true),
                        eqClosed(false))
                .orderBy(lecturePrice.id.asc())
                .fetchResults();

        return new PageImpl<>(lecturePrices.getResults(), pageable, lecturePrices.getTotal());
    }

    public LecturePrice findLecturePriceByMentor(Mentor _mentor, Long lectureId, Long lecturePriceId) {
        return jpaQueryFactory.selectFrom(lecturePrice)
                .innerJoin(lecturePrice.lecture, lecture)
                .fetchJoin()
                .innerJoin(lecture.mentor, mentor)
                .fetchJoin()
                .innerJoin(mentor.user, user)
                .fetchJoin()
                .where(eqMentor(_mentor),
                        this.lecture.id.eq(lectureId),
                        lecturePrice.id.eq(lecturePriceId),
                        eqApproved(true),
                        eqClosed(false))
                .fetchOne();
    }

}
