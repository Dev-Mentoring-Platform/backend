package com.project.mentoridge.modules.purchase.controller.response;

import com.project.mentoridge.modules.lecture.controller.response.*;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class EnrollmentWithEachLectureResponse extends AbstractLectureResponse {

    private Long enrollmentId;
    private boolean checked;
    private boolean finished;

    private LecturePriceResponse lecturePrice;
    private LectureMentorResponse lectureMentor;

    // 후기 작성 여부
    private Boolean reviewed = null;

    public EnrollmentWithEachLectureResponse(Enrollment enrollment) {

        this.enrollmentId = enrollment.getId();
        this.checked = enrollment.isChecked();
        this.finished = enrollment.isFinished();

        Lecture lecture = enrollment.getLecture();
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
        this.lectureMentor = new LectureMentorResponse(lecture.getMentor());

        LecturePrice lecturePrice = enrollment.getLecturePrice();
        this.lecturePrice = new LecturePriceResponse(lecturePrice);
    }
}
