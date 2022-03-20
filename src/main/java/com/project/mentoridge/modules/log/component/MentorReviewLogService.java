package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.log.repository.LogRepository;
import com.project.mentoridge.modules.review.vo.Review;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.util.function.Function;

@Service
public class MentorReviewLogService extends LogService<Review> {

    private final LectureLogService lectureLogService;
    private final MenteeReviewLogService menteeReviewLogService;
    public MentorReviewLogService(LectureLogService lectureLogService, MenteeReviewLogService menteeReviewLogService, LogRepository logRepository) {
        super(logRepository);
        this.lectureLogService = lectureLogService;
        this.menteeReviewLogService = menteeReviewLogService;
    }

    @PostConstruct
    void init() {

        properties.add(new Property("content", "내용"));
        properties.add(new Property("user", "사용자"));
        properties.add(new Property("lecture", "강의"));
        properties.add(new Property("parent", "멘티 리뷰"));

        functions.put("user", review -> review.getUser().getUsername());
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

        Function<Review, String> parentFunc = review -> {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            try {
                sb.append(menteeReviewLogService.getInsertLogContent(review.getParent()));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append(")");
            return sb.toString();
        };
        functions.put("parent", parentFunc);
    }

    @Override
    protected void insert(PrintWriter pw, Review vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Mentor Review] ");
        printInsertLogContent(pw, vo, properties, functions);
    }

    @Override
    protected void update(PrintWriter pw, Review before, Review after) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Mentor Review] ");
        printUpdateLogContent(pw, before, after, properties, functions);
    }

    @Override
    protected void delete(PrintWriter pw, Review vo) throws NoSuchFieldException, IllegalAccessException {

        pw.print("[Mentor Review] ");
        printDeleteLogContent(pw, vo, properties, functions);
    }
}
