package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.vo.Lecture;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class LectureResponse extends AbstractLectureResponse {

    private List<LecturePriceResponse> lecturePrices;
    private LectureMentorResponse lectureMentor;

    // 리뷰 총 개수
    private Long reviewCount = null;
    // 강의 평점
    private Double scoreAverage = null;
    // 수강내역 수
    private Long enrollmentCount = null;

    public LectureResponse(Lecture lecture) {
        this.lectureId = lecture.getId();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.content = lecture.getContent();
        this.difficulty = lecture.getDifficulty();
        this.systems = lecture.getSystems().stream()
                .map(SystemTypeResponse::new).collect(Collectors.toList());
        this.lectureSubjects = lecture.getLectureSubjects().stream()
                .map(LectureSubjectResponse::new).collect(Collectors.toList());
        this.thumbnail = lecture.getThumbnail();
        this.approved = lecture.isApproved();

        this.lecturePrices = lecture.getLecturePrices().stream()
                .map(LecturePriceResponse::new).collect(Collectors.toList());
        this.lectureMentor = new LectureMentorResponse(lecture.getMentor());

//        this.reviewCount = null;
//        this.scoreAverage = null;
//        this.enrollmentCount = null;
    }
}
