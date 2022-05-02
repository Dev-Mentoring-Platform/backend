package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.chat.controller.response.ChatroomResponse;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.modules.account.enums.RoleType.MENTEE;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MenteeChatroomService extends AbstractService {

    private final ChatroomRepository chatroomRepository;
    private final MenteeRepository menteeRepository;
    private final MessageRepository messageRepository;

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
                // chatroomResponse.setLastMessage(messageRepository.findFirstByChatroomIdOrderByIdDesc(chatroom.getId()));
                // chatroomResponse.setUncheckedMessageCount(messageRepository.countAllByChatroomIdAndCheckedIsFalseAndReceiverId(chatroom.getId(), user.getId()));
                return chatroomResponse;
            });
    }

    public ChatroomResponse getChatroom(User user, Long chatroomId) {
        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTEE));
        Chatroom chatroom = chatroomRepository.findByMenteeAndId(mentee, chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.CHATROOM));
        return new ChatroomResponse(chatroom);
    }
}
