package com.project.mentoridge.modules.purchase.controller.response;

import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.Data;

@Data
public class EnrollmentResponse {

    private String mentee;
    private String lectureTitle;

    // TODO - 쿼리
    // TODO : CHECK - Lecture가 이미 영속성 컨텍스트에 존재
    public EnrollmentResponse(Enrollment enrollment) {
        this.mentee = enrollment.getMentee().getUser().getUsername();
        this.lectureTitle = enrollment.getLecture().getTitle();
    }
}
