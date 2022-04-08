package com.project.mentoridge.modules.chat.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.EntityNotFoundException.EntityType;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.service.UserService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.chat.WebSocketHandler;
import com.project.mentoridge.modules.chat.controller.response.ChatroomResponse;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import com.project.mentoridge.modules.log.component.ChatroomLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.CHATROOM;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTEE;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTOR;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatroomService extends AbstractService {

    private final ChatroomRepository chatroomRepository;
    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;

    private final MongoTemplate mongoTemplate;
    private final MessageRepository messageRepository;

    private final ChatroomLogService chatroomLogService;
    private final UserService userService;

    public void createChatroom(User user, Long mentorId) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTEE));

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.MENTOR));

        Chatroom chatroom = Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .build();
        chatroom = chatroomRepository.save(chatroom);
        chatroomLogService.insert(user, chatroom);
        // TODO - CHECK
        WebSocketHandler.chatroomMap.put(chatroom.getId(), new HashMap<>());
    }

    public void closeChatroom(User user, Long chatroomId) {

        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));

        chatroom.close();
        // chatroomRepository.delete(chatroom);
        chatroomLogService.delete(user, chatroom);

        // TODO - 테스트
        // TODO - 웹소켓 세션 삭제
        Map<String, WebSocketSession> sessionMap = WebSocketHandler.chatroomMap.get(chatroomId);
        for (String key : sessionMap.keySet()) {
            WebSocketSession wss = sessionMap.get(key);
            try {
                wss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        WebSocketHandler.chatroomMap.remove(chatroomId);
    }

    private Page<Chatroom> getChatroomsOfMentee(User user, Integer page) {
        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
            .orElseThrow(() -> new UnauthorizedException(MENTEE));
        return chatroomRepository.findByMentee(mentee, getPageRequest(page));
    }

    // TODO - CHECK : Mentor/Mentee EAGER 체크
    // TODO - CHECK : 쿼리 체크
    // TODO - CHECK : Fetch join
    public Page<ChatroomResponse> getChatroomResponsesOfMentee(User user, Integer page) {
        return getChatroomsOfMentee(user, page)
            .map(chatroom -> {
                ChatroomResponse chatroomResponse = new ChatroomResponse(chatroom);
                chatroomResponse.setLastMessage(messageRepository.findFirstByChatroomIdOrderByIdDesc(chatroom.getId()));
                chatroomResponse.setUncheckedMessageCount(messageRepository.countAllByChatroomIdAndCheckedIsFalseAndReceiverId(chatroom.getId(), user.getId()));
                return chatroomResponse;
            });
    }

    // TODO - CHECK : Batch
    private void checkAllMessages(User user, Long chatroomId) {
//        List<Message> uncheckedMessages = mongoTemplate.find(Query.query(Criteria.where("chatroomId").is(chatroomId)
//                .and("checked").is(false).and("username").ne(user.getNickname())), Message.class);
        List<Message> uncheckedMessages = mongoTemplate.find(Query.query(Criteria.where("chatroomId").is(chatroomId)
                .and("checked").is(false).and("receiverId").is(user.getId())), Message.class);
        uncheckedMessages.forEach(message -> {
            message.check();
            messageRepository.save(message);
        });
    }

    public List<Message> getMessagesOfMenteeChatroom(User user, Long chatroomId) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTEE));

        Chatroom chatroom = chatroomRepository.findByMenteeAndId(mentee, chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));

        checkAllMessages(user, chatroomId);
        return messageRepository.findAllByChatroomId(chatroomId);
    }

    private Page<Chatroom> getChatroomsOfMentor(User user, Integer page) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTOR));
        return chatroomRepository.findByMentor(mentor, getPageRequest(page));
    }

    // TODO - CHECK : Fetch join
    public Page<ChatroomResponse> getChatroomResponsesOfMentor(User user, Integer page) {
        return getChatroomsOfMentor(user, page)
                .map(chatroom -> {
                    ChatroomResponse chatroomResponse = new ChatroomResponse(chatroom);
                    chatroomResponse.setLastMessage(messageRepository.findFirstByChatroomIdOrderByIdDesc(chatroom.getId()));
                    chatroomResponse.setUncheckedMessageCount(messageRepository.countAllByChatroomIdAndCheckedIsFalseAndReceiverId(chatroom.getId(), user.getId()));
                    return chatroomResponse;
                });
    }

    public List<Message> getMessagesOfMentorChatroom(User user, Long chatroomId) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTOR));

        Chatroom chatroom = chatroomRepository.findByMentorAndId(mentor, chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));

        checkAllMessages(user, chatroomId);
        return messageRepository.findAllByChatroomId(chatroomId);
    }
/*
    private void accuseChatroom(Long chatroomId) {

        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));
        accuseChatroom(chatroom);
    }*/

    private void accuseChatroom(Chatroom chatroom) {

        chatroom.accused();
        if (chatroom.isClosed()) {
            // TODO
            // chatService.deleteChatroom(chatroomId);
        }
    }

    // TODO - 신고하기
    @Transactional
    public void accuse(User user, Long chatroomId) {

        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));

        User menteeUser = chatroom.getMentee().getUser();
        User mentorUser = chatroom.getMentor().getUser();

        // TODO - TEST
        if (user.equals(menteeUser)) {
            userService.accuseUser(user, mentorUser);
        } else if (user.equals(mentorUser)) {
            userService.accuseUser(user, menteeUser);
        }

        Chatroom before = chatroom.copy();
        accuseChatroom(chatroom);
        chatroomLogService.accuse(user, before, chatroom);
    }

}
