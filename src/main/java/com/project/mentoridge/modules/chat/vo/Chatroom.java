package com.project.mentoridge.modules.chat.vo;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.base.BaseEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(AccessLevel.PRIVATE)
@AttributeOverride(name = "id", column = @Column(name = "chatroom_id"))
@Entity
public class Chatroom extends BaseEntity {

//    @Transient
//    private Set<WebSocketSession> sessions = new HashSet<>();

    // 2022.02.20 - 강의 등록과 상관없이 채팅 가능
//    @ToString.Exclude
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "enrollment_id",
//            referencedColumnName = "enrollment_id",
//            nullable = false,
//            foreignKey = @ForeignKey(name = "FK_CHATROOM_ENROLLMENT_ID"))
//    private Enrollment enrollment;

    // TODO - CHECK : 페치 조인
    // @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id",
            referencedColumnName = "mentor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CHATROOM_MENTOR_ID"))
    private Mentor mentor;

    // TODO - CHECK : 페치 조인
    // @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id",
            referencedColumnName = "mentee_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CHATROOM_MENTEE_ID"))
    private Mentee mentee;

    private int accusedCount = 0;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean closed = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean mentorIn = false;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean menteeIn = false;

    @Builder(access = AccessLevel.PUBLIC)
    private Chatroom(Mentor mentor, Mentee mentee) {
        this.mentor = mentor;
        this.mentee = mentee;
    }

    private Chatroom(Mentor mentor, Mentee mentee, int accusedCount, boolean closed) {
        this.mentor = mentor;
        this.mentee = mentee;
        this.accusedCount = accusedCount;
        this.closed = closed;
    }

    public void accused() {
        this.accusedCount++;
        if (this.accusedCount == 5) {
            close();
        }
    }

    public void close() {
        setClosed(true);
    }

    public void mentorEnter() {
        setMentorIn(true);
    }

    public void mentorOut() {
        setMentorIn(false);
    }

    public void menteeEnter() {
        setMenteeIn(true);
    }

    public void menteeOut() {
        setMenteeIn(false);
    }

    public Chatroom copy() {
        return new Chatroom(mentor, mentee, accusedCount, closed);
    }
/*
    public void enter(WebSocketSession session) {
        log.info("------------ Connection Establised ------------");
        this.sessions.add(session);
    }

    public void sendMessage(TextMessage message, ChatService chatService) {
        this.sessions.parallelStream().forEach(session -> chatService.sendMessage(session, message));
    }

    public void close() {
        setClosed(true);

        log.info("------------ Connection Closed ------------");
        // TODO - 테스트
        // TODO - 웹소켓 세션 삭제
        this.sessions.parallelStream().forEach(session -> {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }*/


}
