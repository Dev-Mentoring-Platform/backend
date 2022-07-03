package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class SimpleLectureResponse {

    private Long id;
    private String title;
    private String subTitle;
    private String introduce;
    private DifficultyType difficulty;
    private List<LectureResponse.SystemTypeResponse> systems;
    // private List<LectureResponse.LecturePriceResponse> lecturePrices;
    private LectureResponse.LecturePriceResponse lecturePrice;
    private List<LectureResponse.LectureSubjectResponse> lectureSubjects;
    private String thumbnail;

    private String mentorNickname;

    // 강의 평점
    private Double scoreAverage = null;
    // 좋아요 수
    private Long pickCount = null;

    public SimpleLectureResponse(Lecture lecture, LecturePrice lecturePrice) {
        this.id = lecture.getId();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.difficulty = lecture.getDifficulty();
        this.systems = lecture.getSystems().stream()
                .map(LectureResponse.SystemTypeResponse::new).collect(Collectors.toList());
//        this.lecturePrices = lecture.getLecturePrices().stream()
//                .map(LectureResponse.LecturePriceResponse::new).collect(Collectors.toList());
        this.lecturePrice = new LectureResponse.LecturePriceResponse(lecturePrice);
        this.lectureSubjects = lecture.getLectureSubjects().stream()
                .map(LectureResponse.LectureSubjectResponse::new).collect(Collectors.toList());
        this.thumbnail = lecture.getThumbnail();

        // TODO - 리팩토링
        this.mentorNickname = lecture.getMentor().getUser().getNickname();
    }
}
