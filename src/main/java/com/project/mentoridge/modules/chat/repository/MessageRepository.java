package com.project.mentoridge.modules.chat.repository;

import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByChatroom(Chatroom chatroom, Pageable pageable);
}
