package com.project.mentoridge.modules.lecture.repository.dto;

import lombok.Data;

@Data
public class LectureMentorQueryDto {

    private Long mentorId;
    private long lectureCount;   // 총 강의 수
    private long reviewCount;    // 리뷰 개수

    public LectureMentorQueryDto(Long mentorId, long lectureCount, long reviewCount) {
        this.mentorId = mentorId;
        this.lectureCount = lectureCount;
        this.reviewCount = reviewCount;
    }
}
