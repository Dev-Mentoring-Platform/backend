package com.project.mentoridge.modules.lecture.repository.dto;

import lombok.Data;

@Data
public class LectureEnrollmentQueryDto {

    private Long lectureId;
    private long enrollmentCount;

    public LectureEnrollmentQueryDto(Long lectureId, long enrollmentCount) {
        this.lectureId = lectureId;
        this.enrollmentCount = enrollmentCount;
    }
}
