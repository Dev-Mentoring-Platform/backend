package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.LecturePriceWithLectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.springframework.data.domain.Page;

public interface EnrollmentService {

    // Page<Lecture> getLecturesOfMentee(User user, Integer page);
    // Page<LectureResponse> getLectureResponsesOfMentee(User user, Integer page);
    Page<LecturePriceWithLectureResponse> getLecturePriceWithLectureResponsesOfMentee(User user, Integer page);

    Page<EnrollmentWithSimpleLectureResponse> getEnrollmentWithSimpleLectureResponses(User user, boolean reviewed, Integer page);

    // 강의 신청
    Enrollment createEnrollment(User user, Long lectureId, Long lecturePriceId);

    // 신청 내역 삭제
    void deleteEnrollment(Enrollment enrollment);

    // 신청 확인
    void check(User user, Long enrollmentId);
}
