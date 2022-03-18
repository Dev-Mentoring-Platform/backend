package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;

@Service
public class MenteeLogService extends LogService<Mentee> {

    public MenteeLogService(LogRepository logRepository) {
        super(logRepository);
    }

    @Override
    protected void insert(PrintWriter pw, Mentee vo) throws NoSuchFieldException, IllegalAccessException {

    }

    @Override
    protected void update(PrintWriter pw, Mentee before, Mentee after) throws NoSuchFieldException, IllegalAccessException {

    }

    @Override
    protected void delete(PrintWriter pw, Mentee vo) throws NoSuchFieldException, IllegalAccessException {

    }
}
