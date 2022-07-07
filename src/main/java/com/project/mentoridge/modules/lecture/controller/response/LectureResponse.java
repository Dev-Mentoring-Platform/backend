package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class LectureResponse extends AbstractLectureResponse {

    private Long id;
    private List<LecturePriceResponse> lecturePrices;

    // 리뷰 총 개수
    private Long reviewCount = null;
    // 강의 평점
    private Double scoreAverage = null;
    // 수강내역 수
    private Long enrollmentCount = null;

    private LectureMentorResponse lectureMentor;

    private Boolean picked = null;

    public LectureResponse(Lecture lecture) {
        this.id = lecture.getId();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.content = lecture.getContent();
        this.difficulty = lecture.getDifficulty();

        this.systems = lecture.getSystems().stream()
                .map(SystemTypeResponse::new).collect(Collectors.toList());

        this.lecturePrices = lecture.getLecturePrices().stream()
                .map(LecturePriceResponse::new).collect(Collectors.toList());

        this.lectureSubjects = lecture.getLectureSubjects().stream()
                .map(LectureSubjectResponse::new).collect(Collectors.toList());

        this.thumbnail = lecture.getThumbnail();
        this.approved = lecture.isApproved();
        this.lectureMentor = new LectureMentorResponse(lecture.getMentor());
    }
}
