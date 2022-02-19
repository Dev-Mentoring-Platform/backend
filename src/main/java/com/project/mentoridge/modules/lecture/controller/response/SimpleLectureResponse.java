package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
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
    private List<LectureResponse.LecturePriceResponse> lecturePrices;
    private List<LectureResponse.LectureSubjectResponse> lectureSubjects;
    private String thumbnail;

    public SimpleLectureResponse(Lecture lecture) {
        this.id = lecture.getId();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.difficulty = lecture.getDifficulty();
        this.systems = lecture.getSystems().stream()
                .map(LectureResponse.SystemTypeResponse::new).collect(Collectors.toList());
        this.lecturePrices = lecture.getLecturePrices().stream()
                .map(LectureResponse.LecturePriceResponse::new).collect(Collectors.toList());
        this.lectureSubjects = lecture.getLectureSubjects().stream()
                .map(LectureResponse.LectureSubjectResponse::new).collect(Collectors.toList());
        this.thumbnail = lecture.getThumbnail();
    }
}
