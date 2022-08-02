package com.project.mentoridge.modules.chat.repository;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.controller.ChatMessage;
import com.project.mentoridge.modules.chat.repository.dto.ChatroomMessageQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class ChatroomMessageQueryRepository {

    private final EntityManager em;

    // lastMessage - 마지막 메시지
    /*
    SELECT * FROM message m
    WHERE m.message_id IN
                    (SELECT MAX(message_id) AS message_id FROM message
                    WHERE chatroom_id IN (1, 2, 3)
                    GROUP BY chatroom_id);
    */
    public Map<Long, ChatMessage> findChatroomMessageQueryDtoMap(List<Long> chatroomIds) {
        List<ChatMessage> lastMessages = em.createQuery("select new com.project.mentoridge.modules.chat.controller.ChatMessage(m.id, m.type, m.chatroom.id, m.sender.id, m.text, m.createdAt) from Message m where m.id in (select max(_m.id) from Message _m where _m.chatroom.id in :chatroomIds group by _m.chatroom.id)")
                .setParameter("chatroomIds", chatroomIds).getResultList();
        return lastMessages.stream().collect(Collectors.toMap(ChatMessage::getChatroomId, chatMessage -> chatMessage));
    }

    // uncheckedMessageCount - '내가' 안 읽은 메시지 개수
    public Map<Long, Long> findChatroomMessageQueryDtoMap(User user, List<Long> chatroomIds) {
        // 내가 sender가 아닌 메시지
        List<ChatroomMessageQueryDto> uncheckedMessageCounts = em.createQuery("select new com.project.mentoridge.modules.chat.repository.dto.ChatroomMessageQueryDto(m.chatroom.id, count(m.id)) from Message m where m.sender.id <> :userId and m.checked is false and m.chatroom.id in :chatroomIds group by m.chatroom.id")
                .setParameter("userId", user.getId())
                .setParameter("chatroomIds", chatroomIds).getResultList();
        return uncheckedMessageCounts.stream()
                .collect(Collectors.toMap(ChatroomMessageQueryDto::getChatroomId, ChatroomMessageQueryDto::getUncheckedMessageCount));
    }

    @Modifying
    @Transactional
    public void updateAllChecked(User user, Long chatroomId) {
        Long userId = user.getId();
        int result = em.createNativeQuery("update message set checked = 1 where chatroom_id = :chatroomId and sender_id <> :userId")
                .setParameter("chatroomId", chatroomId)
                .setParameter("userId", userId).executeUpdate();
        System.out.println(result);
    }
}
