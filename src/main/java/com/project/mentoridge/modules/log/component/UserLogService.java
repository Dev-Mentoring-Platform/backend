package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;

import static com.project.mentoridge.modules.log.vo.Log.buildUpdateLog;

@Service
public class UserLogService extends LogService<User> {

    private static final String USER = "[User] ";

    public UserLogService(LogRepository logRepository) {
        super(logRepository);
        this.title = USER;
    }

    @PostConstruct
    void init() {
        properties.add(new Property("username", "아이디"));
        properties.add(new Property("name", "이름"));
        properties.add(new Property("gender", "성별"));
        properties.add(new Property("birthYear", "생년월일"));
        properties.add(new Property("phoneNumber", "연락처"));
        properties.add(new Property("nickname", "닉네임"));
        properties.add(new Property("image", "이미지"));
        properties.add(new Property("zone", "지역"));
        properties.add(new Property("role", "권한"));
        properties.add(new Property("provider", "OAuth"));
    }

    @Override
    protected void insert(PrintWriter pw, User vo) throws NoSuchFieldException, IllegalAccessException {
        printInsertLogContent(pw, vo, properties);
    }

    @Override
    protected void update(PrintWriter pw, User before, User after) throws NoSuchFieldException, IllegalAccessException {
        printUpdateLogContent(pw, before, after, properties);
    }

    @Override
    protected void delete(PrintWriter pw, User vo) throws NoSuchFieldException, IllegalAccessException {
        printDeleteLogContent(pw, vo, properties);
    }

    public void updatePassword(User user, User before, User after) {
        this.updateStatus(user, before, after, "password", "비밀번호");
    }

    public void updateImage(User user, User before, User after) {
        this.updateStatus(user, before, after, "image", "이미지");
    }

    public void updateFcmToken(User user, User before, User after) {
        this.updateStatus(user, before, after, "fcmToken", "FCM Token");
    }

    public void verifyEmail(User user) {
        this.updateStatus(user, user, "emailVerified", "이메일 인증 여부");
    }

    // TODO - log
    public void findPassword(User user) {
        logRepository.saveLog(buildUpdateLog(user.getUsername(), "[User] 비밀번호 찾기"));
    }
}
