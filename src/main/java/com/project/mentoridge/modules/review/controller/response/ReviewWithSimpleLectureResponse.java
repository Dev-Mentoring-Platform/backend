package com.project.mentoridge.modules.review.controller.response;

import com.project.mentoridge.modules.lecture.controller.response.SimpleLectureResponse;
import com.project.mentoridge.modules.review.vo.Review;
import lombok.ToString;

@ToString(callSuper = true)
public class ReviewWithSimpleLectureResponse extends ReviewResponse {
    // TODO - 상속 or Composition

    private SimpleLectureResponse lecture;

    public ReviewWithSimpleLectureResponse(Review parent) {
        super(parent, null);
        lecture = new SimpleLectureResponse(parent.getLecture());
    }

    public ReviewWithSimpleLectureResponse(Review parent, Review child) {
        super(parent, child);
        lecture = new SimpleLectureResponse(parent.getLecture());
    }

    public SimpleLectureResponse getLecture() {
        return lecture;
    }
}
