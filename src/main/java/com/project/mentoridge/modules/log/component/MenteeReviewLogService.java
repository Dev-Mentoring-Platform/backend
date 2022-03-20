package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.log.repository.LogRepository;
import com.project.mentoridge.modules.review.vo.Review;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;

@Service
public class MenteeReviewLogService extends LogService<Review> {

    private final EnrollmentLogService enrollmentLogService;
    private final LectureLogService lectureLogService;
    public MenteeReviewLogService(LectureLogService lectureLogService, EnrollmentLogService enrollmentLogService, LogRepository logRepository) {
        super(logRepository);
        this.lectureLogService = lectureLogService;
        this.enrollmentLogService = enrollmentLogService;
    }

    @PostConstruct
    void init() {

        properties.add(new Property("score", "평점"));
        properties.add(new Property("content", "내용"));
        properties.add(new Property("user", "사용자"));
        properties.add(new Property("enrollment", "수강 내역"));
        properties.add(new Property("lecture", "강의"));

        functions.put("user", review -> review.getUser().getUsername());
        Function<Review, String> enrollmentFunc = review -> {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            try {
                sb.append(enrollmentLogService.getInsertLogContent(review.getEnrollment()));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append(")");
            return sb.toString();
        };
        functions.put("enrollment", enrollmentFunc);
        Function<Review, String> lectureFunc = review -> {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            try {
                sb.append(lectureLogService.getInsertLogContent(review.getLecture()));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append(")");
            return sb.toString();
        };
        functions.put("lecture", lectureFunc);
    }

    @Override
    protected void insert(PrintWriter pw, Review vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Mentee Review] ");
        printInsertLogContent(pw, vo, properties, functions);
    }

    public String getInsertLogContent(Review vo) throws NoSuchFieldException, IllegalAccessException {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        printInsertLogContent(pw, vo, properties, functions);
        return sw.toString();
    }

    @Override
    protected void update(PrintWriter pw, Review before, Review after) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Mentee Review] ");
        printUpdateLogContent(pw, before, after, properties, functions);
    }

    @Override
    protected void delete(PrintWriter pw, Review vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Mentee Review] ");
        printDeleteLogContent(pw, vo, properties, functions);
    }
}
