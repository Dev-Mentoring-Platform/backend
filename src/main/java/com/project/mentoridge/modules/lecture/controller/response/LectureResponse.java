package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class LectureResponse {

    private Long id;
    private String title;
    private String subTitle;
    private String introduce;
    private String content;
    private DifficultyType difficulty;
    // private String difficultyName;
    private List<SystemTypeResponse> systems;
    private List<LecturePriceResponse> lecturePrices;
    private List<LectureSubjectResponse> lectureSubjects;
    private String thumbnail;
    private Boolean approved = null;

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
        // this.closed = lecture.isClosed();
        this.lectureMentor = new LectureMentorResponse(lecture.getMentor());
    }
}
