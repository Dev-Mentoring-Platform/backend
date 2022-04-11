package com.project.mentoridge.modules.review.controller.response;

import com.project.mentoridge.modules.lecture.controller.response.SimpleLectureResponse;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import lombok.ToString;

@ToString(callSuper = true)
public class ReviewWithSimpleLectureResponse extends ReviewResponse {
    // TODO - 상속 or Composition

    private SimpleLectureResponse lecture;

    public ReviewWithSimpleLectureResponse(MenteeReview parent) {
        super(parent, null);
        lecture = new SimpleLectureResponse(parent.getLecture(), parent.getEnrollment().getLecturePrice());
    }

    public ReviewWithSimpleLectureResponse(MenteeReview parent, MentorReview child) {
        super(parent, child);
        lecture = new SimpleLectureResponse(parent.getLecture(), parent.getEnrollment().getLecturePrice());
    }

    public SimpleLectureResponse getLecture() {
        return lecture;
    }
}
