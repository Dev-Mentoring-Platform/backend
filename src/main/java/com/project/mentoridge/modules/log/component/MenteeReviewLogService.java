package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.repository.LogRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;

@Service
public class MenteeReviewLogService extends LogService<MenteeReview> {

    private static final String MENTEE_REVIEW = "[Mentee Review] ";

    private final EnrollmentLogService enrollmentLogService;
    private final LectureLogService lectureLogService;
    public MenteeReviewLogService(LectureLogService lectureLogService, EnrollmentLogService enrollmentLogService, LogRepository logRepository) {
        super(logRepository);
        this.lectureLogService = lectureLogService;
        this.enrollmentLogService = enrollmentLogService;
        this.title = MENTEE_REVIEW;
    }

    @PostConstruct
    void init() {

        properties.add(new Property("score", "평점"));
        properties.add(new Property("content", "내용"));
        properties.add(new Property("mentee", "멘티"));
        properties.add(new Property("enrollment", "수강 내역"));
        properties.add(new Property("lecture", "강의"));

        functions.put("mentee", review -> review.getMentee().getUser().getNickname());
        Function<MenteeReview, String> enrollmentFunc = review -> {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            try {
                Enrollment enrollment = Hibernate.unproxy(review.getEnrollment(), Enrollment.class);
                sb.append(enrollmentLogService.getInsertLogContent(enrollment));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append(")");
            return sb.toString();
        };
        functions.put("enrollment", enrollmentFunc);
        Function<MenteeReview, String> lectureFunc = review -> {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            try {
                Lecture lecture = Hibernate.unproxy(review.getLecture(), Lecture.class);
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
    protected void insert(PrintWriter pw, MenteeReview vo) throws NoSuchFieldException, IllegalAccessException {
        printInsertLogContent(pw, vo, properties, functions);
    }

    public String getInsertLogContent(MenteeReview vo) throws NoSuchFieldException, IllegalAccessException {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        printInsertLogContent(pw, vo, properties, functions);
        return sw.toString();
    }

    @Override
    protected void update(PrintWriter pw, MenteeReview before, MenteeReview after) throws NoSuchFieldException, IllegalAccessException {
        printUpdateLogContent(pw, before, after, properties, functions);
    }

    @Override
    protected void delete(PrintWriter pw, MenteeReview vo) throws NoSuchFieldException, IllegalAccessException {
        printDeleteLogContent(pw, vo, properties, functions);
    }
}
