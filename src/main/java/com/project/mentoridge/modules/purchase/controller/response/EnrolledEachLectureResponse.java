package com.project.mentoridge.modules.purchase.controller.response;

import com.project.mentoridge.modules.lecture.controller.response.AbstractLectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.LecturePriceResponse;
import com.project.mentoridge.modules.lecture.controller.response.SystemTypeResponse;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class EnrolledEachLectureResponse extends AbstractLectureResponse {

    private LecturePriceResponse lecturePrice;

    // TODO - systems로 수정
    private List<SystemTypeResponse> systemTypes;

    public EnrolledEachLectureResponse(Lecture lecture, LecturePrice lecturePrice) {
        this.lecturePrice = new LecturePriceResponse(lecturePrice);

        this.lectureId = lecture.getId();
        this.thumbnail = lecture.getThumbnail();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.content = lecture.getContent();
        this.difficulty = lecture.getDifficulty();

        this.systemTypes = lecture.getSystems().stream()
                .map(SystemTypeResponse::new)
                .collect(Collectors.toList());
    }
}
