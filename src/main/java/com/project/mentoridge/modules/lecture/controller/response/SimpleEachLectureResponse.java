package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import lombok.Data;

import java.util.stream.Collectors;

@Data
public class SimpleEachLectureResponse extends AbstractLectureResponse {

    private Long id;
    private LecturePriceResponse lecturePrice;

    private String mentorNickname;

    // 강의 평점
    private Double scoreAverage = null;
    // 좋아요 수
    private Long pickCount = null;

    public SimpleEachLectureResponse(Lecture lecture, LecturePrice lecturePrice) {
        this.id = lecture.getId();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.difficulty = lecture.getDifficulty();
        this.systems = lecture.getSystems().stream()
                .map(SystemTypeResponse::new).collect(Collectors.toList());
        this.lecturePrice = new LecturePriceResponse(lecturePrice);
        this.lectureSubjects = lecture.getLectureSubjects().stream()
                .map(LectureSubjectResponse::new).collect(Collectors.toList());
        this.thumbnail = lecture.getThumbnail();
        this.approved = lecture.isApproved();

        // TODO - 리팩토링
        this.mentorNickname = lecture.getMentor().getUser().getNickname();
    }
}
