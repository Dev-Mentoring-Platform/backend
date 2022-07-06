package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithEachLectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleEachLectureResponse;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.springframework.data.domain.Page;

public interface EnrollmentService {

    Page<EnrollmentWithEachLectureResponse> getEnrollmentWithEachLectureResponsesOfMentee(User user, boolean checked, Integer page);
    EachLectureResponse getEachLectureResponseOfMentee(User user, Long enrollmentId);

    Page<EnrollmentWithSimpleEachLectureResponse> getEnrollmentWithSimpleEachLectureResponses(User user, boolean reviewed, Integer page);
    EnrollmentWithSimpleEachLectureResponse getEnrollmentWithSimpleEachLectureResponse(User user, Long enrollmentId);

    // 강의 신청
    Enrollment createEnrollment(User user, Long lectureId, Long lecturePriceId);

    // 신청 내역 삭제
    void deleteEnrollment(Enrollment enrollment);

    // 신청 확인
    void check(User user, Long enrollmentId);

    void finish(User user, Long enrollmentId);
}
