package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.log.repository.LogRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;

@Service
public class EnrollmentLogService extends LogService<Enrollment> {

    private final LectureLogService lectureLogService;
    private final LecturePriceLogService lecturePriceLogService;
    public EnrollmentLogService(LectureLogService lectureLogService, LecturePriceLogService lecturePriceLogService, LogRepository logRepository) {
        super(logRepository);
        this.lectureLogService = lectureLogService;
        this.lecturePriceLogService = lecturePriceLogService;
    }

    @PostConstruct
    void init() {
        properties.add(new Property("mentee", "멘티"));
        properties.add(new Property("lecture", "강의"));
        properties.add(new Property("lecturePrice", "옵션"));

        functions.put("mentee", chatroom -> chatroom.getMentee().getUser().getUsername());
        Function<Enrollment, String> lectureFunc = enrollment -> {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            try {
                sb.append(lectureLogService.getInsertLogContent(enrollment.getLecture()));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append(")");
            return sb.toString();
        };
        functions.put("lecture", lectureFunc);
        Function<Enrollment, String> lecturePriceFunc = enrollment -> {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            try {
                lecturePriceLogService.getLogContent(sb, enrollment.getLecturePrice());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append(")");
            return sb.toString();
        };
        functions.put("lecturePrice", lecturePriceFunc);
    }

    @Override
    protected void insert(PrintWriter pw, Enrollment vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Enrollment] ");
        printInsertLogContent(pw, vo, properties, functions);
    }

    public String getInsertLogContent(Enrollment vo) throws NoSuchFieldException, IllegalAccessException {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        printInsertLogContent(pw, vo, properties, functions);
        return sw.toString();
    }

    // TODO - checked
    @Override
    protected void update(PrintWriter pw, Enrollment before, Enrollment after) throws NoSuchFieldException, IllegalAccessException {
    }

    @Override
    protected void delete(PrintWriter pw, Enrollment vo) throws NoSuchFieldException, IllegalAccessException {
    }
}
