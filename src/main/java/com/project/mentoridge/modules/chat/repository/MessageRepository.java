package com.project.mentoridge.modules.chat.repository;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatroom(Chatroom chatroom);
    Page<Message> findByChatroom(Chatroom chatroom, Pageable pageable);

    List<Message> findBySender(User user);

    @Transactional
    @Modifying
    void deleteBySender(User user);

    @Transactional
    @Modifying
    @Query(value = "delete from Message m where m.chatroom.id in :chatroomIds")
    void deleteByChatroomIds(@Param("chatroomIds") List<Long> chatroomIds);
}
