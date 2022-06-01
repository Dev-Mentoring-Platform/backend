package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

@Service
public class CareerLogService extends LogService<Career> {

    private static final String CAREER = "[Career] ";

    public CareerLogService(LogRepository logRepository) {
        super(logRepository);
        this.title = CAREER;
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
        printInsertLogContent(pw, vo, properties);
    }

    @Override
    protected void update(PrintWriter pw, Career before, Career after) throws NoSuchFieldException, IllegalAccessException {
        printUpdateLogContent(pw, before, after, properties);
    }

    @Override
    protected void delete(PrintWriter pw, Career vo) throws NoSuchFieldException, IllegalAccessException {
        printDeleteLogContent(pw, vo, properties);
    }
}
