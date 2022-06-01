package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.board.vo.Liking;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

@Service
public class LikingLogService extends LogService<Liking> {

    private static final String LIKING = "[Liking] ";

    public LikingLogService(LogRepository logRepository) {
        super(logRepository);
        this.title = LIKING;
    }

    @PostConstruct
    void init() {
        properties.add(new Property("post", "글"));
        functions.put("post", post -> String.valueOf(post.getId()));
    }

    @Override
    protected void insert(PrintWriter pw, Liking vo) throws NoSuchFieldException, IllegalAccessException {
        pw.print(LIKING);
        printInsertLogContent(pw, vo, properties, functions);
    }

    @Override
    protected void update(PrintWriter pw, Liking before, Liking after) throws NoSuchFieldException, IllegalAccessException {
        throw new RuntimeException();
    }

    @Override
    protected void delete(PrintWriter pw, Liking vo) throws NoSuchFieldException, IllegalAccessException {
        pw.print(LIKING);
        printDeleteLogContent(pw, vo, properties, functions);
    }
}
