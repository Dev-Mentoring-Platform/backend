package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class EachLectureResponse extends AbstractLectureResponse {

    private LecturePriceResponse lecturePrice;
    private Long lecturePriceId;

    private boolean closed = false;

    private LectureMentorResponse lectureMentor;

    // 리뷰 총 개수
    private Long reviewCount = null;
    // 강의 평점
    private Double scoreAverage = null;
    // 수강내역 수
    private Long enrollmentCount = null;

    private Boolean picked = null;
    // 좋아요 개수
    private Long pickCount = null;


    public EachLectureResponse(LecturePrice lecturePrice, Lecture lecture) {
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

        this.lecturePrice = new LecturePriceResponse(lecturePrice);
        this.lecturePriceId = lecturePrice.getId();

        this.closed = lecturePrice.isClosed();
        this.lectureMentor = new LectureMentorResponse(lecture.getMentor());

//        this.reviewCount = null;
//        this.scoreAverage = null;
//        this.enrollmentCount = null;
//        this.picked = null;
//        this.pickCount = null;
    }
}
