package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.springframework.data.domain.Page;

public interface EnrollmentService {

    // Page<Lecture> getLecturesOfMentee(User user, Integer page);
    Page<LectureResponse> getLectureResponsesOfMentee(User user, Integer page);

    Page<EnrollmentWithSimpleLectureResponse> getEnrollmentWithSimpleLectureResponses(User user, boolean reviewed, Integer page);

    // 강의 수강
    Enrollment createEnrollment(User user, Long lectureId, Long lecturePriceId);

//    void close(User user, Long lectureId, Long enrollmentId);
    void close(User user, Long lectureId);

    void deleteEnrollment(Enrollment enrollment);
}
