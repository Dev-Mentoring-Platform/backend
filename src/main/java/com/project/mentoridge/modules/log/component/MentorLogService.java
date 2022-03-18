package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

@Service
public class MentorLogService extends LogService<Mentor> {

    public MentorLogService(LogRepository logRepository) {
        super(logRepository);
    }

    @PostConstruct
    void init() {
        properties.add(new Property("user", "사용자"));
        properties.add(new Property("bio", "소개"));
    }

    @Override
    protected void insert(PrintWriter pw, Mentor vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Mentor] ");
        printInsertLogContent(pw, vo, properties);
    }

    @Override
    protected void update(PrintWriter pw, Mentor before, Mentor after) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Mentor] ");
        printUpdateLogContent(pw, before, after, properties);
    }

    @Override
    protected void delete(PrintWriter pw, Mentor vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Mentor] ");
        printDeleteLogContent(pw, vo, properties);
    }
}
