package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.log.repository.LogRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.util.function.Function;

@Service
public class MentorReviewLogService extends LogService<MentorReview> {

    private static final String MENTOR_REVIEW = "[Mentor Review] ";

    private final MenteeReviewLogService menteeReviewLogService;
    public MentorReviewLogService(MenteeReviewLogService menteeReviewLogService, LogRepository logRepository) {
        super(logRepository);
        this.menteeReviewLogService = menteeReviewLogService;
        this.title = MENTOR_REVIEW;
    }

    @PostConstruct
    void init() {

        properties.add(new Property("content", "내용"));
        properties.add(new Property("mentor", "멘토"));
        properties.add(new Property("parent", "멘티 리뷰"));

        functions.put("mentor", review -> review.getMentor().getUser().getNickname());
        Function<MentorReview, String> parentFunc = review -> {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            try {
                MenteeReview menteeReview = Hibernate.unproxy(review.getParent(), MenteeReview.class);
                sb.append(menteeReviewLogService.getInsertLogContent(menteeReview));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append(")");
            return sb.toString();
        };
        functions.put("parent", parentFunc);
    }

    @Override
    protected void insert(PrintWriter pw, MentorReview vo) throws NoSuchFieldException, IllegalAccessException {
        printInsertLogContent(pw, vo, properties, functions);
    }

    @Override
    protected void update(PrintWriter pw, MentorReview before, MentorReview after) throws NoSuchFieldException, IllegalAccessException {
        printUpdateLogContent(pw, before, after, properties, functions);
    }

    @Override
    protected void delete(PrintWriter pw, MentorReview vo) throws NoSuchFieldException, IllegalAccessException {
        printDeleteLogContent(pw, vo, properties, functions);
    }
}
