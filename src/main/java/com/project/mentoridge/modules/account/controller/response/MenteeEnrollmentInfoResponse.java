package com.project.mentoridge.modules.account.controller.response;

import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.controller.response.EnrolledLectureResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

@Data
public class MenteeEnrollmentInfoResponse {

    private Long menteeId;
    private EnrolledLectureResponse lecture;
    private Long reviewId;
    private Long chatroomId;

    @Builder(access = AccessLevel.PUBLIC)
    private MenteeEnrollmentInfoResponse(Long menteeId, Lecture lecture, LecturePrice lecturePrice, Long reviewId, Long chatroomId) {
        this.menteeId = menteeId;
        this.lecture = new EnrolledLectureResponse(lecture, lecturePrice);
        this.reviewId = reviewId;
        this.chatroomId = chatroomId;
    }
}
