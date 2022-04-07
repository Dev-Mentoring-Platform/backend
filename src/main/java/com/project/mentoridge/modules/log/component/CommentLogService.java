package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;

@Service
public class CommentLogService extends LogService<Comment> {

    public CommentLogService(LogRepository logRepository) {
        super(logRepository);
    }

    @Override
    protected void insert(PrintWriter pw, Comment vo) throws NoSuchFieldException, IllegalAccessException {

    }

    @Override
    protected void update(PrintWriter pw, Comment before, Comment after) throws NoSuchFieldException, IllegalAccessException {

    }

    @Override
    protected void delete(PrintWriter pw, Comment vo) throws NoSuchFieldException, IllegalAccessException {

    }
}
