package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;

@Service
public class PostLogService extends LogService<Post> {

    public PostLogService(LogRepository logRepository) {
        super(logRepository);
    }

    @Override
    protected void insert(PrintWriter pw, Post vo) throws NoSuchFieldException, IllegalAccessException {

    }

    @Override
    protected void update(PrintWriter pw, Post before, Post after) throws NoSuchFieldException, IllegalAccessException {

    }

    @Override
    protected void delete(PrintWriter pw, Post vo) throws NoSuchFieldException, IllegalAccessException {

    }
}
