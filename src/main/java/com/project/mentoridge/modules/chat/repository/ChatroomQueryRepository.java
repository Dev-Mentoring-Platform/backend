package com.project.mentoridge.modules.chat.repository;

import com.project.mentoridge.modules.account.vo.*;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.QChatroom;
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

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class ChatroomQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QChatroom chatroom = QChatroom.chatroom;
    private final QMentor mentor = QMentor.mentor;
    private final QMentee mentee = QMentee.mentee;
    private final QUser user = QUser.user;

    private BooleanExpression eqMentor(Mentor _mentor) {
        if (_mentor == null) {
            return null;
        }
        return mentor.eq(_mentor);
    }

    private BooleanExpression eqMentee(Mentee _mentee) {
        if (_mentee == null) {
            return null;
        }
        return mentee.eq(_mentee);
    }

    // 2022.09.06
    // 마지막 메시지를 보낸 시간 기준으로 정렬
    public Page<Chatroom> findByMentorOrderByLastMessagedAtDesc(Mentor _mentor, Pageable pageable) {

        QueryResults<Chatroom> chatrooms = jpaQueryFactory.selectFrom(chatroom)
                .innerJoin(chatroom.mentor, mentor)
                .fetchJoin()
                .innerJoin(mentor.user, user)
                .fetchJoin()
                .innerJoin(chatroom.mentee, mentee)
                .fetchJoin()
                .innerJoin(mentee.user, user)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(eqMentor(_mentor))
                .orderBy(chatroom.lastMessagedAt.desc())
                .fetchResults();
        return new PageImpl<>(chatrooms.getResults(), pageable, chatrooms.getTotal());
    }

    public Page<Chatroom> findByMenteeOrderByLastMessagedAtDesc(Mentee _mentee, Pageable pageable) {

        QueryResults<Chatroom> chatrooms = jpaQueryFactory.selectFrom(chatroom)
                .innerJoin(chatroom.mentor, mentor)
                .fetchJoin()
                .innerJoin(mentor.user, user)
                .fetchJoin()
                .innerJoin(chatroom.mentee, mentee)
                .fetchJoin()
                .innerJoin(mentee.user, user)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(eqMentee(_mentee))
                .orderBy(chatroom.lastMessagedAt.desc())
                .fetchResults();
        return new PageImpl<>(chatrooms.getResults(), pageable, chatrooms.getTotal());
    }

    public List<Chatroom> findByMentorOrderByLastMessagedAtDesc(Mentor _mentor) {
        return jpaQueryFactory.selectFrom(chatroom)
                .innerJoin(chatroom.mentor, mentor)
                .fetchJoin()
                .innerJoin(mentor.user, user)
                .fetchJoin()
                .innerJoin(chatroom.mentee, mentee)
                .fetchJoin()
                .innerJoin(mentee.user, user)
                .fetchJoin()
                .where(eqMentor(_mentor))
                .orderBy(chatroom.lastMessagedAt.desc())
                .fetch();
    }

    public List<Chatroom> findByMenteeOrderByLastMessagedAtDesc(Mentee _mentee) {
        return jpaQueryFactory.selectFrom(chatroom)
                .innerJoin(chatroom.mentor, mentor)
                .fetchJoin()
                .innerJoin(mentor.user, user)
                .fetchJoin()
                .innerJoin(chatroom.mentee, mentee)
                .fetchJoin()
                .innerJoin(mentee.user, user)
                .fetchJoin()
                .where(eqMentee(_mentee))
                .orderBy(chatroom.lastMessagedAt.desc())
                .fetch();
    }
}
