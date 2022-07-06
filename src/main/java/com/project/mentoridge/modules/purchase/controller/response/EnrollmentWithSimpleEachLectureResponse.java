package com.project.mentoridge.modules.purchase.controller.response;


import com.project.mentoridge.modules.lecture.controller.response.SimpleEachLectureResponse;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.ToString;

@ToString(callSuper = true)
public class EnrollmentWithSimpleEachLectureResponse extends EnrollmentResponse {
    // TODO - CHECK : 상속 or Composition
    private SimpleEachLectureResponse lecture;

    public EnrollmentWithSimpleEachLectureResponse(Enrollment enrollment) {
        super(enrollment);
        lecture = new SimpleEachLectureResponse(enrollment.getLecture(), enrollment.getLecturePrice());
    }

    public SimpleEachLectureResponse getLecture() {
        return lecture;
    }
}
