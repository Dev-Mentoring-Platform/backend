package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class _LectureResponse {

    // Long id;
    // TODO - UserResponse 정의
    String thumbnail;
    String title;
    String subTitle;
    String introduce;
    String content;
    DifficultyType difficultyType;
    String difficultyName;
    List<SystemTypeResponse> systemTypes;
    List<LecturePriceResponse> lecturePrices;
    List<LectureSubjectResponse> lectureSubjects;

    public _LectureResponse() {
        // this.id = 0L;
        this.thumbnail = "";
        this.title = "";
        this.subTitle = "";
        this.introduce = "";
        this.content = "";
        this.difficultyType = null;
        this.difficultyName = null;
        this.systemTypes = null;
        this.lecturePrices = null;
        this.lectureSubjects = null;
    }

    public _LectureResponse(Lecture lecture) {
        // this.id = lecture.getId();
        this.thumbnail = lecture.getThumbnail();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.content = lecture.getContent();
        this.difficultyType = lecture.getDifficultyType();
        this.difficultyName = "";
        this.systemTypes = lecture.getSystemTypes().stream()
                .map(SystemTypeResponse::new).collect(Collectors.toList());
        this.lecturePrices = lecture.getLecturePrices().stream()
                .map(LecturePriceResponse::new).collect(Collectors.toList());
        this.lectureSubjects =lecture.getLectureSubjects().stream()
                .map(LectureSubjectResponse::new).collect(Collectors.toList());

    }

    @Value
    public static class LectureSubjectResponse {

        Long learningKindId;
        String learningKind;
        String krSubject;

        public LectureSubjectResponse(LectureSubject lectureSubject) {
            this.learningKindId = lectureSubject.getLearningKind().getLearningKindId();
            this.learningKind = lectureSubject.getLearningKind().getLearningKind();
            this.krSubject = lectureSubject.getKrSubject();
        }
    }

    @Value
    public static class LecturePriceResponse {

        Boolean isGroup;
        Integer groupNumber;
        Integer totalTime;
        Integer pertimeLecture;
        Long pertimeCost;
        Long totalCost;

        public LecturePriceResponse(LecturePrice lecturePrice) {
            this.isGroup = lecturePrice.getIsGroup();
            this.groupNumber = lecturePrice.getGroupNumber();
            this.totalTime = lecturePrice.getTotalTime();
            this.pertimeLecture = lecturePrice.getPertimeLecture();
            this.pertimeCost = lecturePrice.getPertimeCost();
            this.totalCost = lecturePrice.getTotalCost();
        }
    }

    @Value
    public static class SystemTypeResponse {

        String type;
        String name;

        public SystemTypeResponse(SystemType systemType) {
            this.type = systemType.getType();
            this.name = systemType.getName();
        }
    }
}
