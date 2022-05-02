package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.chat.controller.response.ChatroomResponse;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.modules.account.enums.RoleType.MENTOR;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MentorChatroomService extends AbstractService {

    private final ChatroomRepository chatroomRepository;
    private final MentorRepository mentorRepository;

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
                    // chatroomResponse.setLastMessage(messageRepository.findFirstByChatroomIdOrderByIdDesc(chatroom.getId()));
                    // chatroomResponse.setUncheckedMessageCount(messageRepository.countAllByChatroomIdAndCheckedIsFalseAndReceiverId(chatroom.getId(), user.getId()));
                    return chatroomResponse;
                });
    }

    public ChatroomResponse getChatroom(User user, Long chatroomId) {
        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTOR));
        Chatroom chatroom = chatroomRepository.findByMentorAndId(mentor, chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.CHATROOM));
        return new ChatroomResponse(chatroom);
    }
}
