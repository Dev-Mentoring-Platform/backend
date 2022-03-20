package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

@Service
public class ChatroomLogService extends LogService<Chatroom> {

    public ChatroomLogService(LogRepository logRepository) {
        super(logRepository);
    }

    @PostConstruct
    void init() {
        properties.add(new Property("mentor", "멘토"));
        properties.add(new Property("mentee", "멘티"));

        functions.put("mentor", chatroom -> chatroom.getMentor().getUser().getUsername());
        functions.put("mentee", chatroom -> chatroom.getMentee().getUser().getUsername());
    }

    @Override
    protected void insert(PrintWriter pw, Chatroom vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Chatroom] ");
        printInsertLogContent(pw, vo, properties, functions);
    }

    @Override
    protected void update(PrintWriter pw, Chatroom before, Chatroom after) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Chatroom] ");
        printUpdateLogContent(pw, before, after, properties, functions);
    }

    @Override
    protected void delete(PrintWriter pw, Chatroom vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Chatroom] ");
        printDeleteLogContent(pw, vo, properties, functions);
    }

    // TODO - accuse, close
}
