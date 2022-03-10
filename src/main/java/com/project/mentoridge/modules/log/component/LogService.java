package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.User;

public interface LogService {
    // 사용자 활동 이력
    // TODO - MySQL? MongoDB?

    void insert(User user);
    void update(User user);
    void delete(User user);

}
