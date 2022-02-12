package com.project.mentoridge.modules.chat.service;

import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.vo.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessages() {
        return messageRepository.findAll();
    }
}
