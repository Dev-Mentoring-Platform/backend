package com.project.mentoridge.modules.lecture.repository.dto;

import lombok.Data;

@Data
public class LecturePickQueryDto {

    private Long lectureId;
    private long pickCount;

    public LecturePickQueryDto(Long lectureId, long pickCount) {
        this.lectureId = lectureId;
        this.pickCount = pickCount;
    }
}
