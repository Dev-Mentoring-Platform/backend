package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Function;

@Service
public class LectureLogService extends LogService<Lecture> {

    private final LecturePriceLogService lecturePriceLogService;
    public LectureLogService(LecturePriceLogService lecturePriceLogService, LogRepository logRepository) {
        super(logRepository);
        this.lecturePriceLogService = lecturePriceLogService;
    }

    @PostConstruct
    void init() {
        properties.add(new Property("mentor", "멘토"));
        properties.add(new Property("title", "제목"));
        properties.add(new Property("subTitle", "소제목"));
        properties.add(new Property("introduce", "소개"));
        properties.add(new Property("content", "내용"));
        properties.add(new Property("difficulty", "난이도"));
        properties.add(new Property("thumbnail", "이미지"));

        functions.put("mentor", lecture -> lecture.getMentor().getUser().getUsername());

    }

    @Override
    protected void insert(PrintWriter pw, Lecture vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Lecture] ");

        Function<Lecture, String> lecturePriceFunc = lecture -> {
            StringBuilder sb = new StringBuilder();
            List<LecturePrice> lecturePrices = lecture.getLecturePrices();
            int i = 1;
            for (LecturePrice lecturePrice : lecturePrices) {

                if (i == 1) {
                    sb.append("(");
                } else {
                    sb.append("/(");
                }

                try {
                    lecturePriceLogService.getLogContent(sb, lecturePrice);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                sb.append(")");
                i += 1;
            }
            return sb.toString();
        };
        functions.put("lecturePrices", lecturePriceFunc);
        properties.add(new Property("lecturePrices", "가격"));

        Function<Lecture, String> lectureSystemFunc = lecture -> {
            StringBuilder sb = new StringBuilder();
            List<SystemType> systems = lecture.getSystems();
            int i = 1;
            for(SystemType system : systems) {
                if (i != 1) {
                    sb.append("/");
                }
                sb.append(system.getName());
                i += 1;
            }
            return sb.toString();
        };
        functions.put("systems", lectureSystemFunc);
        properties.add(new Property("systems", "온/오프라인"));

        Function<Lecture, String> lectureSubjectFunc = lecture -> {
            StringBuilder sb = new StringBuilder();
            List<LectureSubject> lectureSubjects = lecture.getLectureSubjects();
            int i = 1;
            for (LectureSubject lectureSubject : lectureSubjects) {
                if (i != 1) {
                    sb.append("/");
                }
                sb.append(lectureSubject.getSubject().getKrSubject());
                i += 1;
            }
            return sb.toString();
        };
        functions.put("lectureSubjects", lectureSubjectFunc);
        properties.add(new Property("lectureSubjects", "주제"));

        printInsertLogContent(pw, vo, properties, functions);
    }

    public String getInsertLogContent(Lecture vo) throws NoSuchFieldException, IllegalAccessException {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printInsertLogContent(pw, vo, properties, functions);

        return sw.toString();
    }

    @Override
    protected void update(PrintWriter pw, Lecture before, Lecture after) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Lecture] ");
        printUpdateLogContent(pw, before, after, properties, functions);
    }

    @Override
    protected void delete(PrintWriter pw, Lecture vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Lecture] ");
        printDeleteLogContent(pw, vo, properties, functions);
    }

    public void approve(User user, Lecture lecture) {
        this.updateStatus(user, lecture, "approved", "승인");
    }

    // open 포함
    public void close(User user, Lecture lecture) {
        this.updateStatus(user, lecture, "closed", "종료 여부");
    }

    public void open(User user, Lecture lecture) {
        this.updateStatus(user, lecture, "closed", "종료 여부");
    }
}
