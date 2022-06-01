package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.repository.LogRepository;
import com.project.mentoridge.modules.purchase.vo.Pick;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.util.function.Function;

import static com.project.mentoridge.modules.log.vo.Log.buildDeleteLog;

@Service
public class PickLogService extends LogService<Pick> {

    private static final String PICK = "[Pick] ";

    private final LectureLogService lectureLogService;
    public PickLogService(LectureLogService lectureLogService, LogRepository logRepository) {
        super(logRepository);
        this.lectureLogService = lectureLogService;
        this.title = PICK;
    }

    @PostConstruct
    void init() {
        properties.add(new Property("mentee", "멘티"));
        properties.add(new Property("lecture", "강의"));

        functions.put("mentee", chatroom -> chatroom.getMentee().getUser().getUsername());
        Function<Pick, String> lectureFunc = pick -> {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            try {
                Lecture lecture = Hibernate.unproxy(pick.getLecture(), Lecture.class);
                sb.append(lectureLogService.getInsertLogContent(lecture));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append(")");
            return sb.toString();
        };
        functions.put("lecture", lectureFunc);
    }

    @Override
    protected void insert(PrintWriter pw, Pick vo) throws NoSuchFieldException, IllegalAccessException {
        printInsertLogContent(pw, vo, properties, functions);
    }

    @Override
    protected void update(PrintWriter pw, Pick before, Pick after) throws NoSuchFieldException, IllegalAccessException {
        throw new RuntimeException();
    }

    @Override
    protected void delete(PrintWriter pw, Pick vo) throws NoSuchFieldException, IllegalAccessException {
        printDeleteLogContent(pw, vo, properties, functions);
    }

    public void deleteAll(User user) {
        logRepository.saveLog(buildDeleteLog(user.getUsername(), "[Pick] 위시리스트 전체 삭제"));
    }
}
