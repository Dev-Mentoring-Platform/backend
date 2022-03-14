package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

@Service
public class CareerLogService extends LogService<Career> {

    public CareerLogService(LogRepository logRepository) {
        super(logRepository);
    }

    @PostConstruct
    void init() {

        properties.add(new Property("job", "직업"));
        properties.add(new Property("companyName", "직장명"));
        properties.add(new Property("others", "그 외 경력"));
        properties.add(new Property("license", "자격증"));
    }

    @Override
    protected void insert(PrintWriter pw, Career vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Career] ");
        printInsertLogContent(pw, vo, properties);
    }


    @Override
    protected void update(PrintWriter pw, Career before, Career after) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Career] ");
        printUpdateLogContent(pw, before, after, properties);
    }

    @Override
    protected void delete(PrintWriter pw, Career vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Career] ");
        printDeleteLogContent(pw, vo, properties);
    }
}
