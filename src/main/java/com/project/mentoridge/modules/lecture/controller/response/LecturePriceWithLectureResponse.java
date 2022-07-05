package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class LecturePriceWithLectureResponse {

    private Long lectureId;
    private String title;
    private String subTitle;
    private String introduce;
    private String content;
    private DifficultyType difficulty;
    // private String difficultyName;
    private List<SystemTypeResponse> systems;
    private LecturePriceResponse lecturePrice;
    private Long lecturePriceId;
    private List<LectureSubjectResponse> lectureSubjects;
    private String thumbnail;
    private Boolean approved = null;
    private Boolean closed = null;

    // 리뷰 총 개수
    private Long reviewCount = null;
    // 강의 평점
    private Double scoreAverage = null;
    // 수강내역 수
    private Long enrollmentCount = null;

    private LectureMentorResponse lectureMentor;

    private Boolean picked = null;
    // 좋아요 개수
    private Long pickCount = null;


    public LecturePriceWithLectureResponse(LecturePrice lecturePrice, Lecture lecture) {
        this.lectureId = lecture.getId();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.content = lecture.getContent();
        this.difficulty = lecture.getDifficulty();

        this.systems = lecture.getSystems().stream()
                .map(SystemTypeResponse::new).collect(Collectors.toList());

        this.lecturePrice = new LecturePriceResponse(lecturePrice);
        this.lecturePriceId = lecturePrice.getId();

        this.lectureSubjects = lecture.getLectureSubjects().stream()
                .map(LectureSubjectResponse::new).collect(Collectors.toList());

        this.thumbnail = lecture.getThumbnail();
        this.approved = lecture.isApproved();

        this.closed = lecturePrice.isClosed();
        this.lectureMentor = new LectureMentorResponse(lecture.getMentor());
    }
}
