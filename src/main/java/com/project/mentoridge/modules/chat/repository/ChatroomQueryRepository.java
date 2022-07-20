package com.project.mentoridge.modules.chat.repository;

import com.project.mentoridge.modules.account.vo.*;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.QChatroom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    public Page<Chatroom> findByMentorOrderByIdDesc(Mentor mentor, Pageable pageable) {

//        jpaQueryFactory.selectFrom(chatroom)
//                .innerJoin(chatroom.mentor, mentor)
//                .innerJoin(chatroom.mentee, mentee)
//                .fetchResults();
        return null;
    }

    public Page<Chatroom> findByMenteeOrderByIdDesc(Mentee mentee, Pageable pageable) {
        return null;
    }

    public List<Chatroom> findByMentorOrderByIdDesc(Mentor mentor) {
        return null;
    }

    public List<Chatroom> findByMenteeOrderByIdDesc(Mentee mentee) {
        return null;
    }
}
