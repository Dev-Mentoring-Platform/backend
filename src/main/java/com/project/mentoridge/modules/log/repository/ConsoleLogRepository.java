package com.project.mentoridge.modules.log.repository;

import com.project.mentoridge.modules.log.vo.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile({"dev", "test"})
@Slf4j
@Repository
public class ConsoleLogRepository implements LogRepository {

    @Override
    public void save(Log _log) {
        log.info(_log.toString());
    }
}
