package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

@Service
public class PostLogService extends LogService<Post> {

    public PostLogService(LogRepository logRepository) {
        super(logRepository);
    }

    @PostConstruct
    void init() {
        properties.add(new Property("user", "글 작성자"));
        functions.put("user", post -> post.getUser().getNickname());

        properties.add(new Property("category", "카테고리"));
        properties.add(new Property("title", "제목"));
        properties.add(new Property("content", "내용"));
    }

    @Override
    protected void insert(PrintWriter pw, Post vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Post] ");
        printInsertLogContent(pw, vo, properties, functions);
    }

    @Override
    protected void update(PrintWriter pw, Post before, Post after) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Post] ");
        printUpdateLogContent(pw, before, after, properties, functions);
    }

    @Override
    protected void delete(PrintWriter pw, Post vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Post] ");
        printDeleteLogContent(pw, vo, properties, functions);
    }
}
