package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
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
    private boolean approved = false;
    // private boolean closed = false;

    // 리뷰 총 개수
    private long reviewCount = 0L;
    // 강의 평점
    private double scoreAverage = 0;
    // 수강내역 수
    private long enrollmentCount = 0L;

    private LectureMentorResponse lectureMentor;

    private boolean picked = false;


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

    @NoArgsConstructor
    @Data
    public static class LectureMentorResponse {

        private Long mentorId;
        // 총 강의 수
        private long lectureCount;
        // 리뷰 개수
        private long reviewCount;
        // 닉네임
        private String nickname;
        // 프로필사진
        private String image;

        public LectureMentorResponse(Mentor mentor) {
            this.mentorId = mentor.getId();
            this.lectureCount = 0L;
            this.reviewCount = 0L;
            this.nickname = mentor.getUser().getNickname();
            this.image = mentor.getUser().getImage();
        }
    }

    @NoArgsConstructor
    @Data
    public static class LectureSubjectResponse {

        // private Long learningKindId;
        private String learningKind;
        private String krSubject;

        public LectureSubjectResponse(LectureSubject lectureSubject) {
            this.learningKind = lectureSubject.getSubject().getLearningKind().getName();
            this.krSubject = lectureSubject.getSubject().getKrSubject();
        }
    }

    @NoArgsConstructor
    @Data
    public static class LecturePriceResponse {

        private Long lecturePriceId;
        private Boolean isGroup;
        private Integer numberOfMembers;
        private Long pricePerHour;
        private Integer timePerLecture;
        private Integer numberOfLectures;
        private Long totalPrice;

        private String isGroupStr;
        private String content;

        public LecturePriceResponse(LecturePrice lecturePrice) {
            this.lecturePriceId = lecturePrice.getId();
            this.isGroup = lecturePrice.getIsGroup();
            this.numberOfMembers = lecturePrice.getNumberOfMembers();
            this.pricePerHour = lecturePrice.getPricePerHour();
            this.timePerLecture = lecturePrice.getTimePerLecture();
            this.numberOfLectures = lecturePrice.getNumberOfLectures();
            this.totalPrice = lecturePrice.getTotalPrice();

            this.isGroupStr = lecturePrice.getIsGroup() ? "그룹강의" : "1:1 개인강의";
            this.content = String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", this.pricePerHour, this.timePerLecture, this.numberOfLectures);
        }
    }

    @NoArgsConstructor
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
