package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

@Service
public class EducationLogService extends LogService<Education> {

    public EducationLogService(LogRepository logRepository) {
        super(logRepository);
    }

    @PostConstruct
    void init() {
        properties.add(new Property("educationLevel", "최종학력"));
        properties.add(new Property("schoolName", "학교명"));
        properties.add(new Property("major", "전공"));
        properties.add(new Property("others", "그 외 학력"));
    }

    @Override
    protected void insert(PrintWriter pw, Education vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Education] ");
        printInsertLogContent(pw, vo, properties);
    }

    @Override
    protected void update(PrintWriter pw, Education before, Education after) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Education] ");
        printUpdateLogContent(pw, before, after, properties);
    }

    @Override
    protected void delete(PrintWriter pw, Education vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Education] ");
        printDeleteLogContent(pw, vo, properties);
    }
}
