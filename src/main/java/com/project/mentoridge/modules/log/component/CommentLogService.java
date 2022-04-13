package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

@Service
public class CommentLogService extends LogService<Comment> {

    public CommentLogService(LogRepository logRepository) {
        super(logRepository);
    }

    @PostConstruct
    void init() {
        properties.add(new Property("post", "글"));
        functions.put("post", comment -> comment.getPost().getTitle());

        properties.add(new Property("user", "댓글 작성자"));
        functions.put("user", comment -> comment.getUser().getNickname());

        properties.add(new Property("content", "내용"));
    }

    @Override
    protected void insert(PrintWriter pw, Comment vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Comment] ");
        printInsertLogContent(pw, vo, properties, functions);
    }

    @Override
    protected void update(PrintWriter pw, Comment before, Comment after) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Comment] ");
        printUpdateLogContent(pw, before, after, properties, functions);
    }

    @Override
    protected void delete(PrintWriter pw, Comment vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Comment] ");
        printDeleteLogContent(pw, vo, properties, functions);
    }
}
