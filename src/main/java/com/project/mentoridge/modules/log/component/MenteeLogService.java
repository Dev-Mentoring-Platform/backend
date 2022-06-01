package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.util.function.Function;

@Service
public class MenteeLogService extends LogService<Mentee> {

    private static final String MENTEE = "[Mentee] ";

    public MenteeLogService(LogRepository logRepository) {
        super(logRepository);
        this.title = MENTEE;
    }

    @PostConstruct
    void init() {
        properties.add(new Property("user", "사용자"));
        properties.add(new Property("subjects", "관심 주제"));

        Function<Mentee, String> func = mentee -> mentee.getUser().getUsername();
        functions.put("user", func);
    }

    @Override
    protected void insert(PrintWriter pw, Mentee vo) throws NoSuchFieldException, IllegalAccessException {
        printInsertLogContent(pw, vo, properties, functions);
    }

    @Override
    protected void update(PrintWriter pw, Mentee before, Mentee after) throws NoSuchFieldException, IllegalAccessException {
        printUpdateLogContent(pw, before, after, properties, functions);
    }

    @Override
    protected void delete(PrintWriter pw, Mentee vo) throws NoSuchFieldException, IllegalAccessException {
        printDeleteLogContent(pw, vo, properties, functions);
    }
}
