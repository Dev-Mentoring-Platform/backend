package com.project.mentoridge.modules.review.controller.response;

import com.project.mentoridge.modules.lecture.controller.response.SimpleEachLectureResponse;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import lombok.ToString;

@ToString(callSuper = true)
public class ReviewWithSimpleEachLectureResponse extends ReviewResponse {

    // TODO - 상속 or Composition
    private SimpleEachLectureResponse lecture;

    public ReviewWithSimpleEachLectureResponse(MenteeReview parent) {
        super(parent, null);
        lecture = new SimpleEachLectureResponse(parent.getLecture(), parent.getEnrollment().getLecturePrice());
    }

    public ReviewWithSimpleEachLectureResponse(MenteeReview parent, MentorReview child) {
        super(parent, child);
        lecture = new SimpleEachLectureResponse(parent.getLecture(), parent.getEnrollment().getLecturePrice());
    }

    public SimpleEachLectureResponse getLecture() {
        return lecture;
    }
}
