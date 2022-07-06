package com.project.mentoridge.modules.account.controller.response;

import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.controller.response.EnrolledEachLectureResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

@Data
public class MenteeEnrollmentInfoResponse {

    private Long menteeId;
    private Long enrollmentId;
    private EnrolledEachLectureResponse lecture;
    private Long reviewId;
    private Long chatroomId;

    @Builder(access = AccessLevel.PUBLIC)
    private MenteeEnrollmentInfoResponse(Long menteeId, Long enrollmentId, Lecture lecture, LecturePrice lecturePrice, Long reviewId, Long chatroomId) {
        this.menteeId = menteeId;
        this.enrollmentId = enrollmentId;
        this.lecture = new EnrolledEachLectureResponse(lecture, lecturePrice);
        this.reviewId = reviewId;
        this.chatroomId = chatroomId;
    }
}
