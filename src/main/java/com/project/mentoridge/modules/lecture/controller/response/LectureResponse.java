package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class LectureResponse {

    private Long id;
    private String thumbnail;
    private String title;
    private String subTitle;
    private String introduce;
    private String content;
    private DifficultyType difficultyType;
    // private String difficultyName;
    private List<SystemTypeResponse> systemTypes;
    private List<LecturePriceResponse> lecturePrices;
    private List<LectureSubjectResponse> lectureSubjects;

    // 리뷰 총 개수
    private Integer reviewCount;
    // 강의 평점
    private double scoreAverage;

    private LectureMentorResponse lectureMentor;

    private boolean picked = false;


    public LectureResponse(Lecture lecture) {
        this.id = lecture.getId();
        this.thumbnail = lecture.getThumbnail();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.content = lecture.getContent();
        this.difficultyType = lecture.getDifficultyType();

        this.systemTypes = lecture.getSystemTypes().stream()
                .map(SystemTypeResponse::new).collect(Collectors.toList());

        this.lecturePrices = lecture.getLecturePrices().stream()
                .map(LecturePriceResponse::new).collect(Collectors.toList());

        this.lectureSubjects = lecture.getLectureSubjects().stream()
                .map(LectureSubjectResponse::new).collect(Collectors.toList());

        this.lectureMentor = new LectureMentorResponse(lecture.getMentor());
    }

    @Data
    public static class LectureMentorResponse {

        private Long mentorId;
        // 총 강의 수
        private Integer lectureCount;
        // 리뷰 개수
        private Integer reviewCount;
        // 닉네임
        private String nickname;
        // 프로필사진
        private String image;

        public LectureMentorResponse(Mentor mentor) {
            this.mentorId = mentor.getId();
            this.lectureCount = 0;
            this.reviewCount = 0;
            this.nickname = mentor.getUser().getNickname();
            this.image = mentor.getUser().getImage();
        }
    }

    @Data
    public static class LectureSubjectResponse {

        private Long learningKindId;
        private String learningKind;
        private String krSubject;

        public LectureSubjectResponse(LectureSubject lectureSubject) {
            this.learningKindId = lectureSubject.getLearningKind().getLearningKindId();
            this.learningKind = lectureSubject.getLearningKind().getLearningKind();
            this.krSubject = lectureSubject.getKrSubject();
        }
    }

    @Data
    public static class LecturePriceResponse {

        private Long lecturePriceId;
        private Boolean isGroup;
        private Integer groupNumber;
        private Integer totalTime;
        private Integer pertimeLecture;
        private Long pertimeCost;
        private Long totalCost;

        private String isGroupStr;
        private String content;

        public LecturePriceResponse(LecturePrice lecturePrice) {
            this.lecturePriceId = lecturePrice.getId();
            this.isGroup = lecturePrice.getIsGroup();
            this.groupNumber = lecturePrice.getGroupNumber();
            this.totalTime = lecturePrice.getTotalTime();
            this.pertimeLecture = lecturePrice.getPertimeLecture();
            this.pertimeCost = lecturePrice.getPertimeCost();
            this.totalCost = lecturePrice.getTotalCost();

            this.isGroupStr = lecturePrice.getIsGroup() ? "그룹강의" : "1:1 개인강의";
            this.content = String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", this.pertimeCost, this.pertimeLecture, this.totalTime);
        }
    }

    @Data
    public static class SystemTypeResponse {

        private String type;
        private String name;

        public SystemTypeResponse(SystemType systemType) {
            this.type = systemType.getType();
            this.name = systemType.getName();
        }
    }
}
