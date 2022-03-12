package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.User;

import java.io.PrintStream;

public interface LogService<T> {
    // 사용자 활동 이력
    // TODO - MySQL? MongoDB?
    // TODO - PrintStream, PrintWriter
    void select(PrintStream ps, User user, Long id);
    void insert(PrintStream ps, User user, T vo);
    void update(PrintStream ps, User user, T before, T after);
    void delete(PrintStream ps, User user, T vo);

}
