package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.User;
import org.springframework.stereotype.Service;

import java.io.PrintStream;

@Service
public class CareerLogService implements LogService<Career> {

    @Override
    public void select(PrintStream ps, User user, Long id) {

    }

    @Override
    public void insert(PrintStream ps, User user, Career vo) {

    }

    @Override
    public void update(PrintStream ps, User user, Career before, Career after) {

    }

    @Override
    public void delete(PrintStream ps, User user, Career vo) {

    }
}
