package com.project.mentoridge.modules.lecture.repository.dto;

import lombok.Data;

@Data
public class LectureMentorQueryDto {

    private Long mentorId;
    private Long lectureCount;   // 총 강의 수
    private Long reviewCount;    // 리뷰 개수

    public LectureMentorQueryDto(Long mentorId, Long lectureCount, Long reviewCount) {
        this.mentorId = mentorId;
        this.lectureCount = lectureCount;
        this.reviewCount = reviewCount;
    }
}
