package com.project.mentoridge.modules.lecture.repository.dto;

import lombok.Data;

@Data
public class LectureMentorQueryDto {

    private Long mentorId;
    private int lectureCount;   // 총 강의 수
    private int reviewCount;    // 리뷰 개수

    public LectureMentorQueryDto(Long mentorId, int lectureCount, int reviewCount) {
        this.mentorId = mentorId;
        this.lectureCount = lectureCount;
        this.reviewCount = reviewCount;
    }
}
