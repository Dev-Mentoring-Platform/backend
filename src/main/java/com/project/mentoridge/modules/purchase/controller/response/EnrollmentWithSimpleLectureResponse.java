package com.project.mentoridge.modules.purchase.controller.response;


import com.project.mentoridge.modules.lecture.controller.response.SimpleLectureResponse;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.ToString;

@ToString(callSuper = true)
public class EnrollmentWithSimpleLectureResponse extends EnrollmentResponse {
    // TODO - CHECK : 상속 or Composition
    private SimpleLectureResponse lecture;

    public EnrollmentWithSimpleLectureResponse(Enrollment enrollment) {
        super(enrollment);
        lecture = new SimpleLectureResponse(enrollment.getLecture(), enrollment.getLecturePrice());
    }

    public SimpleLectureResponse getLecture() {
        return lecture;
    }
}
