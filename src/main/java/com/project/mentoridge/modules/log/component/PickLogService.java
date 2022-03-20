package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.log.repository.LogRepository;
import com.project.mentoridge.modules.purchase.vo.Pick;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.util.function.Function;

@Service
public class PickLogService extends LogService<Pick> {

    private final LectureLogService lectureLogService;
    public PickLogService(LectureLogService lectureLogService, LogRepository logRepository) {
        super(logRepository);
        this.lectureLogService = lectureLogService;
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
                sb.append(lectureLogService.getInsertLogContent(pick.getLecture()));
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

        pw.print("[Pick] ");
        printInsertLogContent(pw, vo, properties, functions);
    }

    @Override
    protected void update(PrintWriter pw, Pick before, Pick after) throws NoSuchFieldException, IllegalAccessException {
    }

    @Override
    protected void delete(PrintWriter pw, Pick vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Pick] ");
        printDeleteLogContent(pw, vo, properties, functions);
    }

    // TODO
    public void deleteAll(PrintWriter pw, Pick vo) throws NoSuchFieldException, IllegalAccessException {

    }
}
