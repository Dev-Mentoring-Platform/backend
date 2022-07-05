package com.project.mentoridge.modules.purchase.controller.response;

import com.project.mentoridge.modules.lecture.controller.response.LecturePriceResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.SystemTypeResponse;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class EnrolledLectureResponse {

    private Long lectureId;
    private String thumbnail;
    private String title;
    private String subTitle;
    private String introduce;
    private String content;
    private LecturePriceResponse lecturePrice;
    private List<SystemTypeResponse> systemTypes;

    public EnrolledLectureResponse(Lecture lecture, LecturePrice lecturePrice) {
        this.lectureId = lecture.getId();
        this.thumbnail = lecture.getThumbnail();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.content = lecture.getContent();
        this.lecturePrice = new LecturePriceResponse(lecturePrice);
        this.systemTypes = lecture.getSystems().stream()
                .map(SystemTypeResponse::new)
                .collect(Collectors.toList());
    }
}
