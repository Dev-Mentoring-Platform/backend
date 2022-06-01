package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.inquiry.vo.Inquiry;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

@Service
public class InquiryLogService extends LogService<Inquiry> {

    private static final String INQUIRY = "[Inquiry] ";

    public InquiryLogService(LogRepository logRepository) {
        super(logRepository);
        this.title = INQUIRY;
    }

    @PostConstruct
    void init() {
        properties.add(new Property("type", "유형"));
        properties.add(new Property("title", "제목"));
        properties.add(new Property("content", "내용"));
    }

    @Override
    protected void insert(PrintWriter pw, Inquiry vo) throws NoSuchFieldException, IllegalAccessException {
        printInsertLogContent(pw, vo, properties);
    }

    @Override
    protected void update(PrintWriter pw, Inquiry before, Inquiry after) throws NoSuchFieldException, IllegalAccessException {
        printUpdateLogContent(pw, before, after, properties);
    }

    @Override
    protected void delete(PrintWriter pw, Inquiry vo) throws NoSuchFieldException, IllegalAccessException {
        printDeleteLogContent(pw, vo, properties);
    }
}
