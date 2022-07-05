package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class _LectureResponse {

    // Long id;
    // TODO - UserResponse 정의
    String title;
    String subTitle;
    String introduce;
    String content;
    DifficultyType difficulty;
    // String difficultyName;
    List<SystemTypeResponse> systems;
    List<LecturePriceResponse> lecturePrices;
    List<LectureSubjectResponse> lectureSubjects;
    String thumbnail;

    public _LectureResponse() {
        // this.id = 0L;
        this.title = "";
        this.subTitle = "";
        this.introduce = "";
        this.content = "";
        this.difficulty = null;
        // this.difficultyName = null;
        this.systems = null;
        this.lecturePrices = null;
        this.lectureSubjects = null;

        this.thumbnail = "";
    }

    public _LectureResponse(Lecture lecture) {
        // this.id = lecture.getId();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.content = lecture.getContent();
        this.difficulty = lecture.getDifficulty();
        // this.difficultyName = "";
        this.systems = lecture.getSystems().stream()
                .map(SystemTypeResponse::new).collect(Collectors.toList());
        this.lecturePrices = lecture.getLecturePrices().stream()
                .map(LecturePriceResponse::new).collect(Collectors.toList());
        this.lectureSubjects =lecture.getLectureSubjects().stream()
                .map(LectureSubjectResponse::new).collect(Collectors.toList());
        this.thumbnail = lecture.getThumbnail();
    }
/*
    @Value
    public static class LectureSubjectResponse {

        // Long learningKindId;
        String learningKind;
        String krSubject;

        public LectureSubjectResponse(LectureSubject lectureSubject) {
            this.learningKind = lectureSubject.getSubject().getLearningKind().getName();
            this.krSubject = lectureSubject.getSubject().getKrSubject();
        }
    }

    @Value
    public static class LecturePriceResponse {

        Boolean isGroup;
        Integer numberOfMembers;
        Long pricePerHour;
        Integer timePerLecture;
        Integer numberOfLectures;
        Long totalPrice;

        public LecturePriceResponse(LecturePrice lecturePrice) {
            this.isGroup = lecturePrice.getIsGroup();
            this.numberOfMembers = lecturePrice.getNumberOfMembers();
            this.pricePerHour = lecturePrice.getPricePerHour();
            this.timePerLecture = lecturePrice.getTimePerLecture();
            this.numberOfLectures = lecturePrice.getNumberOfLectures();
            this.totalPrice = lecturePrice.getTotalPrice();
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
    }*/
}
