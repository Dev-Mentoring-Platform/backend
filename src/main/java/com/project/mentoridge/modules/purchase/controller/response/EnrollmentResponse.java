package com.project.mentoridge.modules.purchase.controller.response;

import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import lombok.Data;

@Data
public class EnrollmentResponse {

    private String mentee;
    private String lectureTitle;
    // 강의 등록일자 추가
    private String createdAt;

    // TODO - 쿼리
    // TODO : CHECK - Lecture가 이미 영속성 컨텍스트에 존재
    public EnrollmentResponse(Enrollment enrollment) {
        this.mentee = enrollment.getMentee().getUser().getNickname();
        this.lectureTitle = enrollment.getLecture().getTitle();
        this.createdAt = LocalDateTimeUtil.getDateTimeToString(enrollment.getCreatedAt());
    }
}
