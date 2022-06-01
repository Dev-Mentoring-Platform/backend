package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

@Service
public class PostLogService extends LogService<Post> {

    private static final String POST = "[Post] ";

    public PostLogService(LogRepository logRepository) {
        super(logRepository);
        this.title = POST;
    }

    @PostConstruct
    void init() {
        properties.add(new Property("user", "글 작성자"));
        functions.put("user", post -> post.getUser().getNickname());

        properties.add(new Property("category", "카테고리"));
        properties.add(new Property("title", "제목"));
        properties.add(new Property("content", "내용"));
        properties.add(new Property("image", "이미지"));
    }

    @Override
    protected void insert(PrintWriter pw, Post vo) throws NoSuchFieldException, IllegalAccessException {
        printInsertLogContent(pw, vo, properties, functions);
    }

    @Override
    protected void update(PrintWriter pw, Post before, Post after) throws NoSuchFieldException, IllegalAccessException {
        printUpdateLogContent(pw, before, after, properties, functions);
    }

    @Override
    protected void delete(PrintWriter pw, Post vo) throws NoSuchFieldException, IllegalAccessException {
        printDeleteLogContent(pw, vo, properties, functions);
    }
}
