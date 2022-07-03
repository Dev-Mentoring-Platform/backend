package com.project.mentoridge.modules.review.controller.response;

import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import lombok.Data;

@Data
public class ReviewResponse {

    public ReviewResponse(MenteeReview parent, MentorReview child) {
        this.menteeReviewId = parent.getId();
        this.enrollmentId = parent.getEnrollment().getId();
        this.score = parent.getScore();
        this.content = parent.getContent();
        this.username = parent.getMentee().getUser().getUsername();
        this.userNickname = parent.getMentee().getUser().getNickname();
        this.userImage = parent.getMentee().getUser().getImage();
        this.createdAt = LocalDateTimeUtil.getDateTimeToString(parent.getCreatedAt());
        this.child = child != null ? new ChildReviewResponse(child) : null;
    }

    private Long menteeReviewId;
    private Long enrollmentId;
    private Integer score;
    private String content;
    private String username;
    private String userNickname;
    private String userImage;
    private String createdAt;
    private ChildReviewResponse child;

    @Data
    public static class ChildReviewResponse {

        private Long mentorReviewId;
        private String content;
        private String username;
        private String userNickname;
        private String userImage;
        private String createdAt;

        public ChildReviewResponse(MentorReview review) {
            if (review != null) {
                this.mentorReviewId = review.getId();
                this.content = review.getContent();
                this.username = review.getMentor().getUser().getUsername();
                this.userNickname = review.getMentor().getUser().getNickname();
                this.userImage = review.getMentor().getUser().getImage();
                this.createdAt = LocalDateTimeUtil.getDateTimeToString(review.getCreatedAt());
            }
        }
    }
}
