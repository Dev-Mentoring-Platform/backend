package com.project.mentoridge.modules.purchase.repository;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.lecture.vo.QLecture;
import com.project.mentoridge.modules.lecture.vo.QLecturePrice;
import com.project.mentoridge.modules.purchase.controller.response.PickWithSimpleEachLectureResponse;
import com.project.mentoridge.modules.purchase.vo.Pick;
import com.project.mentoridge.modules.purchase.vo.QPick;
import com.querydsl.core.QueryResults;
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
public class PickQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QPick pick = QPick.pick;

    private final QLecture lecture = QLecture.lecture;
    private final QLecturePrice lecturePrice = QLecturePrice.lecturePrice;

    public Page<PickWithSimpleEachLectureResponse> findPicks(Mentee mentee, Pageable pageable) {

        QueryResults<Pick> picks = jpaQueryFactory.selectFrom(pick)
                .innerJoin(pick.lecturePrice, lecturePrice)
                .fetchJoin()
                .innerJoin(pick.lecture, lecture)
                .fetchJoin()
                .where(pick.mentee.eq(mentee))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<PickWithSimpleEachLectureResponse> results = picks.getResults().stream().map(PickWithSimpleEachLectureResponse::new).collect(Collectors.toList());
        return new PageImpl<>(results, pageable, picks.getTotal());
    }
}
