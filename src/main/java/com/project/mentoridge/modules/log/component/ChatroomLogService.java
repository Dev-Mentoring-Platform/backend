package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.log.repository.LogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

@Slf4j
@Service
public class ChatroomLogService extends LogService<Chatroom> {

    private static final String CHATROOM = "[Chatroom] ";

    public ChatroomLogService(LogRepository logRepository) {
        super(logRepository);
        this.title = CHATROOM;
    }

    @PostConstruct
    void init() {
        properties.add(new Property("mentor", "멘토"));
        functions.put("mentor", chatroom -> chatroom.getMentor().getUser().getUsername());
        properties.add(new Property("mentee", "멘티"));
        functions.put("mentee", chatroom -> chatroom.getMentee().getUser().getUsername());

        // properties.add(new Property("closed", "종료"));
    }

    @Override
    protected void insert(PrintWriter pw, Chatroom vo) throws NoSuchFieldException, IllegalAccessException {
        printInsertLogContent(pw, vo, properties, functions);
    }

    @Override
    protected void update(PrintWriter pw, Chatroom before, Chatroom after) throws NoSuchFieldException, IllegalAccessException {
        printUpdateLogContent(pw, before, after, properties, functions);
    }

    @Override
    protected void delete(PrintWriter pw, Chatroom vo) throws NoSuchFieldException, IllegalAccessException {
        printDeleteLogContent(pw, vo, properties, functions);
    }
/*
    // TODO - accuse
    public void accuse(User user, Chatroom vo) {

        try {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            printAccuseLogContent(pw, vo);
            logRepository.save(buildUpdateLog(user.getUsername(), sw.toString()));

        } catch(Exception e) {
            log.error("log-error : [update] user : {}, vo : {}", user.getUsername(), vo.toString());
            e.printStackTrace();
        }
    }

    private void printAccuseLogContent(PrintWriter pw, Chatroom vo) {
        pw.print(String.format("[Chatroom] Chatroom-%s is accused", vo.getId()));
    }*/

    public void accuse(User user, Chatroom before, Chatroom after) {
        this.updateStatus(user, before, after, "accusedCount", "신고 횟수");
    }

    public void close(User user, Chatroom before, Chatroom after) {
        this.updateStatus(user, before, after, "closed", "종료");
    }
}
