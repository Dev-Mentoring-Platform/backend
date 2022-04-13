<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
package com.project.mentoridge.modules.purchase.repository;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.lecture.vo.QLecture;
import com.project.mentoridge.modules.lecture.vo.QLecturePrice;
import com.project.mentoridge.modules.purchase.controller.response.PickWithSimpleLectureResponse;
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

    public Page<PickWithSimpleLectureResponse> findPicks(Mentee mentee, Pageable pageable) {

        QueryResults<Pick> picks = jpaQueryFactory.selectFrom(pick)
                .innerJoin(pick.lecturePrice, lecturePrice)
                .fetchJoin()
                .innerJoin(pick.lecture, lecture)
                .fetchJoin()
                .where(pick.mentee.eq(mentee))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<PickWithSimpleLectureResponse> results = picks.getResults().stream().map(PickWithSimpleLectureResponse::new).collect(Collectors.toList());
        return new PageImpl<>(results, pageable, picks.getTotal());
    }
=======
package com.project.mentoridge.modules.purchase.repository;public class PickQueryRepository {
>>>>>>> e3fd6a89e80deff5eb69b442fb807180fe2f2235
=======
package com.project.mentoridge.modules.purchase.repository;public class PickQueryRepository {
>>>>>>> e3fd6a8... pick 리스트 API 수정 및 전체 테스트
=======
package com.project.mentoridge.modules.purchase.repository;public class PickQueryRepository {
>>>>>>> e3fd6a89e80deff5eb69b442fb807180fe2f2235
}
