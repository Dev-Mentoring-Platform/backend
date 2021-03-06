package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.project.mentoridge.modules.log.vo.Log.buildUpdateLog;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginLogService {

    private final LogRepository logRepository;

    public String login(User user) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            this.printLoginLogContent(pw, user);
            String log = sw.toString();
            logRepository.saveLog(buildUpdateLog(user.getUsername(), log));

            return log;

        } catch(Exception e) {
            log.error("log-error : [login] user : {}", user.getUsername());
            e.printStackTrace();
        }

        return null;
    }

    private void printLoginLogContent(PrintWriter pw, User user) {
        pw.print(String.format("[Login] user : %s", user.getUsername()));
    }
}
